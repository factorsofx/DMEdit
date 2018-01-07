package com.factorsofx.dmedit.parser.map;

import com.factorsofx.dmedit.parser.code.ObjectNode;

import java.util.Map;
import java.util.stream.Collectors;

public class Instance
{
    private ObjectNode baseNode;
    private Map<String, String> varMods;

    @Override
    public String toString()
    {
        return baseNode.toString() +
                "{" +
                varMods.entrySet().stream()
                        .map((e) -> e.getKey() + " = " + e.getValue()).collect(Collectors.joining(";")) +
                "}";
    }
}
