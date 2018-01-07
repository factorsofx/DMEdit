package com.factorsofx.dmedit.parser.code;

public interface ObjectTreeListener
{
    void onNodeAdded(ObjectNode node);

    void onNodeRemoved(ObjectNode node);
}
