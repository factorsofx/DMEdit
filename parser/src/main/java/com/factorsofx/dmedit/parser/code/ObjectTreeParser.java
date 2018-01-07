package com.factorsofx.dmedit.parser.code;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The main parser. Probably shitty. Inspired by the FastDMM one, but less shitty?
 * <strike>The eventual goal is to try to make it multithreaded</strike> That probably won't happen.
 * <em>STILL WIP - DO NOT USE</em>
 */
public class ObjectTreeParser
{
    private File dme;

    private ObjectTree tree;

    /**
     * Group 1 is file to include
     */
    private static final Pattern INCLUDE_PATTERN = Pattern.compile("\\s*?#include\\s+\"(.+)\"\\s*");

    /**
     * Group 1 is the name of the macro
     * Group 2 is the value of the macro
     */
    private static final Pattern DEFINE_PATTERN = Pattern.compile("\\s*?as#define\\s+([^\\s]+)\\s+(.+)\\s*");

    /**
     * Group 1 is the macro to undefine
     */
    private static final Pattern UNDEF_PATTERN = Pattern.compile("\\s*?#undef\\s+([^\\s]+)\\s*");

    /**
     * Group 1 is the name of the proc
     * Group 2 is the parameters
     */
    private static final Pattern PROC_PATTERN = Pattern.compile("^\\s*?(?:proc/)?([\\w/]+?)\\s*\\((.*)\\)\\s*");

    /**
     * Group 1 is the type and flags and name of the var
     * Group 2 is what was assigned
     */
    private static final Pattern VAR_PATTERN = Pattern.compile("^\\s*?(?:var/)?([\\w/]+)\\s*=\\s*(.+)");

    public ObjectTreeParser(File dme, ObjectTree tree)
    {
        this(dme);
        this.tree = tree;
    }

    public ObjectTreeParser(File dme)
    {
        this.dme = dme;
        this.tree = new ObjectTree();
    }

    public Future<ObjectTree> parse() throws IOException
    {
        CompletableFuture<ObjectTree> future = new CompletableFuture<>();
        Thread t = new Thread(() ->
        {
            try
            {
                // Begin by parsing stddef.dm, then parse the DME
                //subParse(tree, new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/stddef.dm"))), null);
                subParse(tree, new BufferedReader(new FileReader(dme)), dme.toPath());
            }
            catch(IOException e)
            {
                throw new UncheckedIOException(e);
            }
            future.complete(tree);
        });
        t.start();
        return future;
    }

    public ObjectTree parseSynchronously()
    {
        try
        {
            // Begin by parsing stddef.dm, then parse the DME
            //subParse(tree, new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/stddef.dm"))), null);
            subParse(tree, new BufferedReader(new FileReader(dme)), dme.toPath());
        }
        catch(IOException e)
        {
            throw new UncheckedIOException(e);
        }
        return tree;
    }

    /**
     * Parses an individual file.
     *
     * @param reader The reader of the file
     * @throws IOException If an IOException occurs reading from the buffer
     */
    private void subParse(ObjectTree tree, BufferedReader reader, Path currentFile) throws IOException
    {
        List<String> lines = new ArrayList<>(); // Relatively small, so ArrayList will outperform LinkedList
        StringBuilder lineBuffer = new StringBuilder(); // Buffer for multilines and incomplete parens

        Deque<ParseLevel> parseStack = new ArrayDeque<>(); // Stack for nested operations
        parseStack.addLast(ParseLevel.BASE);

        // Variables for parsing the var defs - this could get ugly
        List<List<String>> pathList = new ArrayList<>();        // Keeps track of the current path sections - explained later
        int indentDepth;                                        // For parsing tabs
        int lowestProcDepth = -1;                               // Lowest indent depth for the current proc, or -1 if no proc
        StringBuilder fullPathBuilder = new StringBuilder();    // Builds the full path of a line
        List<CharSequence> splitLine;                           // For storing the line split on '/'
        List<String> subPath;                                   // The part of the path given in the current line
        CharSequence restOfTheLine;                             // The part of the line that doesn't define the path

        boolean inComment = false; // Flag for a multiline comment

        String currentLine; // Current line
        int lineCount = 0; // Current line #
        while((currentLine = reader.readLine()) != null)
        {
            lineCount++;

            if(beginsAfterSpaces(currentLine, "#"))
            {
                // Preprocessor statement, so buffer verbatim
                lines.add(currentLine);
                continue;
            }

            // Iterate over every character for initial parse
            for(int i = 0; i < currentLine.length(); i++)
            {
                //Check for end of multiline comment
                if(inComment)
                {
                    if(checkSequence("*/", currentLine, i))
                    {
                        inComment = false;
                        i++;
                    }
                }
                else
                {
                    ParseLevel depth = parseStack.getLast();

                    // Comment Checks - Comments don't occur in strings, including single-quote ones (files)
                    if(depth != ParseLevel.SINGLE_QUOTES && depth != ParseLevel.DOUBLE_QUOTES && depth != ParseLevel.MULTILINE_STRING)
                    {
                        if(checkSequence("/*", currentLine, i))
                        {
                            inComment = true;
                            i++;
                            continue;
                        }
                        if(checkSequence("//", currentLine, i))
                        {
                            break;
                        }
                    }

                    // Not a comment, so buffer the character then check the expression stack
                    lineBuffer.append(currentLine.charAt(i));

                    // String escapes first
                    if(depth == ParseLevel.SINGLE_QUOTES || depth == ParseLevel.DOUBLE_QUOTES || depth == ParseLevel.MULTILINE_STRING)
                    {
                        if(currentLine.charAt(i) == '\\')
                        {
                            i++;
                            continue;
                        }
                    }

                    // Expression end testing
                    switch(depth)
                    {
                        case PARENS:
                            if(currentLine.charAt(i) == ')')
                            {
                                parseStack.removeLast();
                                continue;
                            }
                            break;
                        case INDEX:
                        case STRING_INLINE:
                            if(currentLine.charAt(i) == ']')
                            {
                                parseStack.removeLast();
                                continue;
                            }
                            break;
                        case SINGLE_QUOTES:
                            if(currentLine.charAt(i) == '\'')
                            {
                                parseStack.removeLast();
                                continue;
                            }
                            break;
                        case DOUBLE_QUOTES:
                            if(currentLine.charAt(i) == ('\"'))
                            {
                                parseStack.removeLast();
                                continue;
                            }
                            break;
                        case MULTILINE_STRING:
                            if(checkSequence("\"}", currentLine, i))
                            {
                                i++;
                                parseStack.removeLast();
                                continue;
                            }
                            break;
                    }
                    // Expression begin testing
                    if(depth == ParseLevel.BASE || depth == ParseLevel.PARENS || depth == ParseLevel.INDEX || depth == ParseLevel.STRING_INLINE)
                    {
                        if(currentLine.charAt(i) == '(')
                        {
                            parseStack.addLast(ParseLevel.PARENS);
                            continue;
                        }
                        else if(currentLine.charAt(i) == '[')
                        {
                            parseStack.addLast(ParseLevel.INDEX);
                            continue;
                        }
                        else if(currentLine.charAt(i) == '\"')
                        {
                            parseStack.addLast(ParseLevel.DOUBLE_QUOTES);
                            continue;
                        }
                        else if(currentLine.charAt(i) == '\'')
                        {
                            parseStack.addLast(ParseLevel.SINGLE_QUOTES);
                            continue;
                        }
                        else if(checkSequence("{\"", currentLine, i))
                        {
                            i++;
                            parseStack.addLast(ParseLevel.MULTILINE_STRING);
                            continue;
                        }
                    }
                    // String embeds
                    if(depth == ParseLevel.DOUBLE_QUOTES || depth == ParseLevel.MULTILINE_STRING)
                    {
                        if(currentLine.charAt(i) == '[')
                        {
                            parseStack.addLast(ParseLevel.STRING_INLINE);
                        }
                    }
                }
            }
            // End of character loop - check multiline string, hanging parens, and linebreak escapes before storing line
            if(parseStack.getLast() == ParseLevel.BASE)
            {
                if(!isBlank(lineBuffer))
                    lines.add(lineBuffer.toString());
                lineBuffer.setLength(0);
            }
            else if(parseStack.getLast() == ParseLevel.MULTILINE_STRING)
            {
                lineBuffer.append("\n");
            }
            else if(!(parseStack.getLast() == ParseLevel.PARENS || currentLine.endsWith("\\")))
            {
                System.err.println("Bad line state after parsing line " + currentFile + ":" + lineCount + ", discarding line. Last parse state: " + parseStack.getLast());
                lineBuffer.setLength(0);
            }
        }

        lineCount = 0;

        // All lines validated and processed - parse tree now
        for(String line : lines)
        {
            if(isBlank(line))
            {
                continue;
            }

            if(beginsAfterSpaces(line, "#"))
            {
                // Preprocessor directives first, these short-circuit all other line processing
                if(beginsAfterSpaces(line, "#include"))
                {
                    Matcher matcher = INCLUDE_PATTERN.matcher(line);
                    if(matcher.find())
                    {
                        String includedPath = matcher.group(1);
                        if(includedPath.endsWith(".dm") || includedPath.endsWith(".dme"))
                        {
                            File includedFile = new File(currentFile.getParent().toFile(), FilenameUtils.separatorsToSystem(includedPath));
                            if(includedFile.exists())
                            {
                                //System.out.println("Parsing included DM file " + includedFile.getPath());
                                subParse(tree, new BufferedReader(new FileReader(includedFile)), includedFile.toPath());
                            }
                        }
                        else
                        {
                            System.err.println(currentFile + " includes non-DM file " + includedPath + ", ignoring");
                        }
                    }
                }
                else if(beginsAfterSpaces(line, "#define"))
                {
                    Matcher matcher = DEFINE_PATTERN.matcher(line);
                    if(matcher.find())
                    {
                        String defineName = matcher.group(1);
                        if(defineName.equals("FILE_DIR"))
                        {
                            tree.addFileDir(Paths.get(matcher.group(2)));
                            // TODO: FILE_DIR
                        }
                        else
                        {
                            tree.addMacro(matcher.group(1), matcher.group(2));
                        }
                    }
                }
                else if(beginsAfterSpaces(line, "#undef"))
                {
                    Matcher matcher = UNDEF_PATTERN.matcher(line);
                    if(matcher.find())
                    {
                        tree.removeMacro(matcher.group(1));
                    }
                }
                continue;
            }
            // Not a preprocessor statement, process normally.

            // Indent processing: pathList is a list of String lists. Each String list represents a subsection of
            // a path, like ["obj", "machinery", "vending"] for example. For each tab indent, a new String list
            // is added to the list at the corresponding spot. For example:
            /*
            /obj/machinery      pathList: {["obj", "machinery"]}
                atmospherics    pathList: {["obj", "machinery"], ["atmospherics"]}
                    binary      pathList: {["obj", "machinery"], ["atmospherics"], ["binary"]}
             */

            fullPathBuilder.setLength(0);
            indentDepth = getIndentDepth(line);
            if(indentDepth <= lowestProcDepth)
            {
                lowestProcDepth = -1;
            }

            if(!(lowestProcDepth >= 0 && indentDepth >= lowestProcDepth))
            {
                // Clears the path list down to the current level
                for(int i = pathList.size() - 1; i >= indentDepth; i--)
                {
                    pathList.remove(i);
                }
                // Ensures enough nodes are present for the current indent

                subPath = new ArrayList<>();

                splitLine = splitPath(line);

                for(CharSequence pathSequence : splitLine)
                {
                    String pathSection = pathSequence.toString();
                    if(pathSection.isEmpty() || pathSection.equals("const") || pathSection.equals("static") || pathSection.equals("global") || pathSection.equals("tmp"))
                    {
                        continue;
                    }
                    if(PROC_PATTERN.matcher(pathSection).matches())
                    {
                        if(lowestProcDepth < 0)
                        {
                            lowestProcDepth = indentDepth;
                        }
                        break;
                    }
                    if(pathSection.contains("var") || pathSection.contains("="))
                    {
                        break;
                    }
                    subPath.add(pathSection.trim());
                }
                for(int i = pathList.size(); i <= indentDepth; i++)
                {
                    pathList.add(Collections.emptyList());
                }
                pathList.set(indentDepth, subPath);

                // The full path of the line, tabs and the current stuff
                fullPathBuilder.append(pathList.stream().flatMap(Collection::stream).collect(Collectors.joining("/")));

                // The actual "meat" of the line, with no directory
                restOfTheLine = splitLine.get(splitLine.size() - 1);
                Matcher matcher = VAR_PATTERN.matcher(restOfTheLine);
                if(matcher.find())
                {
                    //System.out.println("Var found in " + fullPathBuilder + ": " + matcher.group(1) + " = " + matcher.group(2));
                    String varName = matcher.group(1).trim();
                    String varVal = matcher.group(2).trim();
                    tree.getOrCreateObjectNode(fullPathBuilder.toString().trim()).setVar(varName, varVal);
                }
            }

            lineCount++;
        }
    }

    // A few utilities to make code more concise

    private static StringBuilder splitPathBuffer = new StringBuilder();

    /**
     * Splits the given line into its path segments, and the remainder of the line as the last element.
     *
     * @param path Path to split
     * @return An array of path segments and then the line
     */
    private static List<CharSequence> splitPath(CharSequence path)
    {
        splitPathBuffer.setLength(0);
        List<CharSequence> pathSections = new ArrayList<>();

        for(int i = 0; i < path.length(); i++)
        {
            if(Character.isLetterOrDigit(path.charAt(i)) || path.charAt(i) == '_')
            {
                splitPathBuffer.append(path.charAt(i));
            }
            else
            {
                if(path.charAt(i) == '/')
                {
                    pathSections.add(splitPathBuffer.toString());
                    splitPathBuffer.setLength(0);
                }
                else
                {
                    splitPathBuffer.append(path.subSequence(i, path.length()));
                    pathSections.add(splitPathBuffer.toString());
                    return pathSections;
                }
            }
        }
        if(splitPathBuffer.length() > 0)
        {
            pathSections.add(splitPathBuffer.toString());
        }

        return pathSections;
    }


    // The following methods are static methods designed to (hopefully) perform faster
    // than what you could get with Java String methods, since they're slightly more
    // purpose-built. Testing should be done.

    /**
     * Checks if the sequence of characters <code>subString</code> is present at index <code>index</code> in string <code>fullString</code>.
     *
     * @param fullString The full string
     * @param subString  The subsequence to check
     * @param index      Where to subString
     * @return <code>true</code> if the sequence of characters in <code>subString</code> is present at index <code>index</code> in <code>fullString</code>, else false
     */
    private static boolean checkSequence(CharSequence subString, CharSequence fullString, int index)
    {
        if(index < 0 || index > fullString.length() - subString.length())
        {
            return false;
        }
        for(int i = 0; i < subString.length(); i++)
        {
            if(!(subString.charAt(i) == fullString.charAt(i + index))) return false;
        }
        return true;
    }

    /**
     * A replacement for <code>string.trim().isEmpty()</code> that doesn't allocate a new string.
     *
     * @param str String to check
     * @return <code>true</code> if the string is empty, <code>false</code> if it isn't.
     */
    private static boolean isBlank(CharSequence str)
    {
        for(int i = 0; i < str.length(); i++)
        {
            if(!Character.isWhitespace(str.charAt(i))) return false;
        }
        return true;
    }

    /**
     * A replacement for <code>string.trim().beginsWith()</code> that doesn't allocate a new string.
     *
     * @param str String to check
     * @param beg Beginning to look for
     * @return <code>true</code> if <code>str</code>'s first non-whitespace characters are <code>beg</code>
     */
    private static boolean beginsAfterSpaces(CharSequence str, CharSequence beg)
    {
        int i;
        for(i = 0; i < str.length(); i++)
        {
            if(str.charAt(i) != ' ' && str.charAt(i) != '\t')
            {
                break;
            }
        }
        if(i + beg.length() > str.length()) return false;
        for(int j = 0; j < beg.length(); j++)
        {
            if(str.charAt(i + j) != beg.charAt(j)) return false;
        }
        return true;
    }

    /**
     * Returns the amount of tabs at the beginning of the line
     *
     * @param line String to check the indent depth of
     * @return Amount of tabs before the first character or end of the given string
     */
    private int getIndentDepth(CharSequence line)
    {
        int i;
        for(i = 0; i < line.length(); i++)
        {
            if(!(line.charAt(i) == '\t')) break;
        }
        return i;
    }

    /**
     * Used to keep track of expressions and such in the initial parse
     */
    private enum ParseLevel
    {
        SINGLE_QUOTES,
        DOUBLE_QUOTES,
        INDEX,
        STRING_INLINE,
        MULTILINE_STRING,
        PARENS,
        BASE
    }
}