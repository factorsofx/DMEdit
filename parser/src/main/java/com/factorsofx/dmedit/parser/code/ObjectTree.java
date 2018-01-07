package com.factorsofx.dmedit.parser.code;

import java.nio.file.Path;
import java.util.*;

public class ObjectTree
{
    // region Byond Builtins

    // Catch-all for other stuff
    private ObjectNode rootNode = new ObjectNode(null, "");

    private ObjectNode datum = new ObjectNode(rootNode, "datum");
    {
        datum.setVar("tag", "null");
    }

    private ObjectNode atom = new ObjectNode(datum, "atom");
    {
        atom.setVar("alpha", "255");
        atom.setVar("appearance_flags", "0");
        atom.setVar("blend_mode", "0");
        atom.setVar("color", "null");
        atom.setVar("density", "0");
        atom.setVar("desc", "null");
        atom.setVar("dir", "2");
        atom.setVar("gender", "\"neuter\"");
        atom.setVar("icon", "null");
        atom.setVar("icon_state", "null");
        atom.setVar("infra_luminosity", "0");
        atom.setVar("invisibility", "0");
        atom.setVar("layer", "1");
        atom.setVar("luminosity", "0");
        atom.setVar("maptext", "null");
        atom.setVar("maptext_width", "32");
        atom.setVar("maptext_height", "32");
        atom.setVar("maptext_x", "0");
        atom.setVar("maptext_y", "0");
        atom.setVar("mouse_drag_pointer", "0");
        atom.setVar("mouse_drop_pointer", "1");
        atom.setVar("mouse_drop_zone", "0");
        atom.setVar("mouse_opacity", "1");
        atom.setVar("mouse_over_pointer", "0");
        atom.setVar("name", "null");
        atom.setVar("opacity", "0");
        atom.setVar("overlays", "list()");
        atom.setVar("override", "0");
        atom.setVar("pixel_x", "0");
        atom.setVar("pixel_y", "0");
        atom.setVar("pixel_z", "0");
        atom.setVar("plane", "0");
        atom.setVar("suffix", "null");
        atom.setVar("transform", "null");
        atom.setVar("underlays", "list()");
        atom.setVar("verbs", "list()");
    }

    private ObjectNode movable = new ObjectNode(atom, "movable");
    {
        movable.setVar("animate_movement", "1");
        movable.setVar("bound_x", "0");
        movable.setVar("bound_y", "0");
        movable.setVar("bound_width", "32");
        movable.setVar("bound_height", "32");
        movable.setVar("glide_size", "0");
        movable.setVar("screen_loc", "null");
        movable.setVar("step_size", "32");
        movable.setVar("step_x", "0");
        movable.setVar("step_y", "0");
    }

    private ObjectNode area = new ObjectNode(atom, "area");
    {
        area.setVar("layer", "1");
        area.setVar("luminosity", "1");
    }

    private ObjectNode turf = new ObjectNode(atom, "turf");
    {
        turf.setVar("layer", "2");
    }

    private ObjectNode obj = new ObjectNode(movable, "obj");
    {
        obj.setVar("layer", "3");
    }

    private ObjectNode mob = new ObjectNode(movable, "mob");
    {
        mob.setVar("ckey", "null");
        mob.setVar("density", "1");
        mob.setVar("key", "null");
        mob.setVar("layer", "4");
        mob.setVar("see_in_dark", "2");
        mob.setVar("see_infrared", "0");
        mob.setVar("see_invisible", "0");
        mob.setVar("sight", "0");
    }

    private ObjectNode world = new ObjectNode(datum, "world");
    {
        world.setVar("turf", "/turf");
        world.setVar("mob", "/mob");
        world.setVar("area", "/area");
    }

    // endregion

    private List<Path> fileDirs;
    private Map<String, String> macros;
    private Map<String, ObjectNode> pathCache;

    private List<ObjectTreeListener> listeners = new ArrayList<>();

    public ObjectTree()
    {
        fileDirs = new ArrayList<>();
        macros = new HashMap<>();
        pathCache = new HashMap<>();
    }

    public ObjectNode getOrCreateObjectNode(String path)
    {
        String trimmedPath = path.trim();

        ObjectNode current;
        if((current = pathCache.get(trimmedPath)) != null)
        {
            return current;
        }

        String[] splitPath = trimmedPath.split("/");
        int startIndex = 0;

        if(splitPath[0].isEmpty()) startIndex++;

        if(splitPath.length > startIndex)
        {
            ObjectNode currentNode;

            String startNodeName = splitPath[startIndex];
            switch(startNodeName)
            {
                case "datum":
                    currentNode = datum;
                    break;
                case "atom":
                    currentNode = atom;
                    break;
                case "area":
                    currentNode = area;
                    break;
                case "turf":
                    currentNode = turf;
                    break;
                case "obj":
                    currentNode = obj;
                    break;
                case "mob":
                    currentNode = mob;
                    break;
                default:
                    currentNode = datum;
                    break;
            }

            startIndex++;

            for(int i = startIndex; i < splitPath.length; i++)
            {
                // Iterates through the path, getting or creating children as it goes.
                // Remember, ObjectNode::new automatically adds the new node to its parent
                Optional<ObjectNode> optNode = currentNode.getChild(splitPath[i]);
                if(optNode.isPresent())
                {
                    currentNode = optNode.get();
                }
                else
                {
                    ObjectNode newNode = currentNode = new ObjectNode(currentNode, splitPath[i]);
                    listeners.forEach((listener -> listener.onNodeAdded(newNode)));
                }
            }

            return currentNode;
        }
        else
        {
            return datum;
        }
    }

    public void addFileDir(Path path)
    {
        fileDirs.add(path);
    }

    public ObjectNode getRootNode()
    {
        return datum;
    }

    public void addListener(ObjectTreeListener listener)
    {
        listeners.add(listener);
    }

    void addMacro(String name, String val)
    {
        macros.put(name, val);
    }

    void removeMacro(String name)
    {
        macros.remove(name);
    }

    boolean isMacro(String macro)
    {
        return macros.containsKey(macro);
    }

    String getMacro(String macro)
    {
        return macros.get(macro);
    }
}
