package com.factorsofx.dmedit.parser;

import com.factorsofx.dmedit.parser.byond.ObjectNode;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * I took this code from FastDMM and cleaned it up a bit.
 */
public class FastDMMParser {
    private boolean isCommenting = false;
    private boolean inMultilineString = false;
    private int multilineStringDepth = 0;
    private int parenthesisDepth = 0;
    private int stringDepth = 0;
    private int stringExpDepth = 0;
    private int parenthesesDepth = 0;
    private int[] arrayDepth = new int[50];

    private ObjectTree tree;

    private static final CachedPattern QUOTES_PATTERN = new CachedPattern("^\"(.*)\"$");
    private static final CachedPattern DEFINE_PATTERN = new CachedPattern("#define +([\\d\\w]+) +(.+)");
    private static final CachedPattern UNDEF_PATTERN  = new CachedPattern("#undef[ \\t]*([\\d\\w]+)");
    private static final CachedPattern MACRO_PATTERN  = new CachedPattern("(?<![\\d\\w\"])\\w+(?![\\d\\w\"])");

    public FastDMMParser() {
        tree = new ObjectTree();
    }

    private FastDMMParser(ObjectTree tree) {
        this.tree = tree;
    }

    public void parseDME(File file) throws IOException {
        // Parse stddef.dm for macros and such.
        doSubParse(new BufferedReader(new InputStreamReader(FastDMMParser.class.getResourceAsStream("/stddef.dm"))), Paths.get("stddef.dm"));

        doParse(new BufferedReader(new FileReader(file)), file.toPath(), true);
    }

    private void doParse(BufferedReader br, Path currentFile, boolean isMainFile) throws IOException
    {
        String line = null;
        ArrayList<String> lines = new ArrayList<>();
        StringBuilder runOn = new StringBuilder();;
        int includeCount = 0;
        // This part turns spaces into tabs, strips all the comments, and puts multiline statements on one line.
        while ((line = br.readLine()) != null) {
            line = stripComments(line);
            line = line.replaceAll("\\t", " ");
            if(!line.trim().isEmpty()) {
                if(line.endsWith("\\")) {
                    line = line.substring(0, line.length() - 1);
                    runOn.append(line);
                } else if(inMultilineString) {
                    runOn.append(line);
                    runOn.append("\\n");
                } else if(parenthesisDepth > 0) {
                    runOn.append(line);
                } else {
                    runOn.append(line);
                    line = runOn.toString();
                    runOn.setLength(0);
                    lines.add(line);
                    if(isMainFile && line.trim().startsWith("#include"))
                        includeCount++;
                }
            }
        }
        br.close();

        ArrayList<String> pathTree = new ArrayList<>();

        int currentInclude = 0;

        for (String line1 : lines) {
            line = line1;
            // Process #include, #define, and #undef
            if (line.trim().startsWith("#")) {
                line = line.trim();
                if (line.startsWith("#include")) {
                    String path = line.split("\"")[1];
                    if (path.endsWith(".dm") || path.endsWith(".dme")) {
                        File includeFile = new File(currentFile.getParent().toFile(), FilenameUtils.separatorsToSystem(path));
                        if (!includeFile.exists()) {
                            System.err.println(currentFile.getFileName() + " references a nonexistent file: " + includeFile.getAbsolutePath());
                            continue;
                        }
                        doSubParse(new BufferedReader(new FileReader(includeFile)), includeFile.toPath());
                    }
                    if (isMainFile) {
                        currentInclude++;
                    }
                }
                else if (line.startsWith("#define")) {
                    Matcher m = DEFINE_PATTERN.getMatcher(line);
                    if (m.find()) {
                        String group = m.group(1);
                        if (group.equals("FILE_DIR")) {
                            Matcher quotes = QUOTES_PATTERN.getMatcher(m.group(2));
                            if (quotes.find()) {
                                // 2 ways this can't happen:
                                // Somebody intentionally placed broken FILE_DIR defines.
                                // It's the . FILE_DIR, which has no quotes, and we don't need.
                                tree.addFileDir(Paths.get(FilenameUtils.separatorsToSystem(quotes.group(1))));
                            }

                        } else {
                            tree.addMacro(m.group(1), m.group(2).replace("$", "\\$"));
                        }
                    }
                }
                else if (line.startsWith("#undef")) {
                    Matcher m = UNDEF_PATTERN.getMatcher(line);
                    if (m.find() && tree.isMacro(m.group(1))) {
                        tree.removeMacro(m.group(1));
                    }
                }

                continue;
            }
            // How far is this line indented?
            int level = 0;
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == ' ')
                    level++;
                else
                    break;
            }
            // Rebuild the path tree.
            for (int j = pathTree.size(); j <= level; j++)
                pathTree.add("");
            pathTree.set(level, cleanPath(line.trim()));
            if (pathTree.size() > level + 1)
                for (int j = pathTree.size() - 1; j > level; j--)
                    pathTree.remove(j);
            String fullPath = "";
            for (String c : pathTree)
                fullPath += c;
            // Now, split it again, and rebuild it again, but only figure out how big the object itself is.
            String[] divided = fullPath.split("\\/");
            String affectedObjectPath = "";
            for (String item : divided) {
                if (item.isEmpty()) {
                    continue;
                }
                if (item.equalsIgnoreCase("static") || item.equalsIgnoreCase("global") || item.equalsIgnoreCase("tmp"))
                    continue;
                if (item.equals("proc") || item.equals("verb") || item.equals("var")) {
                    break;
                }
                if (item.contains("=") || item.contains("(")) {
                    break;
                }
                affectedObjectPath += "/" + item;
            }
            ObjectNode item = tree.getOrCreateObjectNode(affectedObjectPath);
            if (fullPath.contains("(") && fullPath.indexOf("(") < fullPath.lastIndexOf("/"))
                continue;
            fullPath = fullPath.replaceAll("/tmp", ""); // Let's avoid giving a shit about whether the var is tmp, static, or global.
            fullPath = fullPath.replaceAll("/static", "");
            fullPath = fullPath.replaceAll("/global", "");
            // Parse the var definitions.
            if (fullPath.contains("var/") ||
                    (fullPath.contains("=") && (!fullPath.contains("(") || fullPath.indexOf("(") > fullPath.indexOf("=")))) {
                String[] split = Pattern.compile("=").split(fullPath, 2);
                String varname = split[0].substring(split[0].lastIndexOf("/") + 1, split[0].length()).trim();
                if (split.length > 1) {
                    String val = split[1].trim();
                    String origVal = "";
                    while (!origVal.equals(val)) {
                        origVal = val;
                        // Trust me, this is the fastest way to parse the macros.
                        Matcher m = MACRO_PATTERN.getMatcher(val);
                        StringBuffer outVal = new StringBuffer();
                        while (m.find()) {
                            if (tree.isMacro(m.group(0)))
                                m.appendReplacement(outVal, tree.getMacro(m.group(0)));
                            else
                                m.appendReplacement(outVal, m.group(0));
                        }
                        m.appendTail(outVal);
                        val = outVal.toString();
                    }
                    /*// Parse additions.
					Matcher m = Pattern.compile("([\\d\\.]+)[ \\t]*\\+[ \\t]*([\\d\\.]+)").matcher(val);
					StringBuffer outVal = new StringBuffer();
					while(m.find()) {
						m.appendReplacement(outVal, (Float.parseFloat(m.group(1)) + Float.parseFloat(m.group(2)))+"");
					}
					m.appendTail(outVal);
					val = outVal.toString();
					// Parse subtractions.
					m = Pattern.compile("([\\d\\.]+)[ \\t]*\\-[ \\t]*([\\d\\.]+)").matcher(val);
					outVal = new StringBuffer();
					while(m.find()) {
						m.appendReplacement(outVal, (Float.parseFloat(m.group(1)) - Float.parseFloat(m.group(2)))+"");
					}
					m.appendTail(outVal);
					val = outVal.toString();*/

                    item.setVar(varname, val);
                } else {
                    item.setVar(varname, "");
                }
            }
        }

        // Reset variables
        isCommenting = false;
        inMultilineString = false;
        multilineStringDepth = 0;
        parenthesisDepth = 0;
        stringDepth = 0;
        stringExpDepth = 0;
        parenthesesDepth = 0;
        arrayDepth = new int[50];
    }

    private void doSubParse(BufferedReader newFileReader, Path newFile) throws IOException {
        FastDMMParser parser = new FastDMMParser(tree);
        parser.doParse(newFileReader, newFile, false);
    }

    private String stripComments(String s)
    {
        StringBuilder o = new StringBuilder();
        for(int i = 0; i < s.length(); i++) {
            char pC = ' ';
            if(i - 1 >= 0)
                pC = s.charAt(i - 1);
            char ppC = ' ';
            if(i - 2 >= 0)
                ppC = s.charAt(i - 2);
            char c = s.charAt(i);
            char nC = ' ';
            if(i + 1 < s.length())
                nC = s.charAt(i + 1);
            if(!isCommenting) {
                if(c == '/' && nC == '/' && stringDepth == 0)
                    break;
                if(c == '/' && nC == '*' && stringDepth == 0) {
                    isCommenting = true;
                    continue;
                }
                if(c == '"' && nC == '}' && (pC != '\\' || ppC == '\\') && stringDepth == multilineStringDepth && inMultilineString)
                    inMultilineString = false;
                if(c == '"' && (pC != '\\' || ppC == '\\') && stringDepth != stringExpDepth && (!inMultilineString || multilineStringDepth != stringDepth)) {
                    stringDepth--;
                } else if(c == '"' && stringDepth == stringExpDepth && (!inMultilineString || multilineStringDepth != stringDepth)) {
                    stringDepth++;
                    if(pC == '{') {
                        inMultilineString = true;
                        multilineStringDepth = stringDepth;
                    }
                }
                if(c == '[' && stringDepth == stringExpDepth)
                    arrayDepth[stringExpDepth]++;
                else if(c == '[' && (pC != '\\' || ppC == '\\') && stringDepth != stringExpDepth)
                    stringExpDepth++;

                if(c == ']' && arrayDepth[stringExpDepth] != 0)
                    arrayDepth[stringExpDepth]--;
                else if(c == ']' && stringDepth > 0 && stringDepth == stringExpDepth)
                    stringExpDepth--;
                if(c == '(' && stringDepth == stringExpDepth)
                    parenthesisDepth++;
                if(c == ')' && stringDepth == stringExpDepth)
                    parenthesisDepth--;
                o.append(c);
            }
            else {
                if(c == '*' && nC == '/') {
                    isCommenting = false;
                    i++;
                }
            }

        }
        return o.toString();
    }

    private static String cleanPath(String s)
    {
        // Makes sure that paths start with a slash, and don't end with a slash.
        if(!s.startsWith("/"))
            s = "/" + s;
        if(s.endsWith("/"))
            s = s.substring(0, s.length() - 1);
        return s;
    }

    public ObjectTree getTree()
    {
        return tree;
    }

    /**
     * Simple wrapper to lazily compile a Pattern
     */
    private static class CachedPattern {

        private String regexp;
        private Pattern cachedPattern;

        /**
         * Constructs a new cached regex pattern.
         *
         * @param regexp The regex.
         */
        CachedPattern(String regexp) {
            this.regexp = regexp;
        }

        /**
         * Returns the Java Regex Pattern object that has been cached.
         *
         * @return The Pattern.
         */
        Pattern getPattern() {
            if (cachedPattern == null) {
                cachedPattern = Pattern.compile(regexp);
            }
            return cachedPattern;
        }

        /**
         * Obtains the matcher for this pattern.
         *
         * @param input the input to match to.
         * @return the matcher.
         */
        Matcher getMatcher(CharSequence input) {
            return getPattern().matcher(input);
        }

    }
}