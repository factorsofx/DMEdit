package com.factorsofx.dmedit.parser;

import com.factorsofx.dmedit.parser.byond.ObjectNode;

public interface ObjectTreeListener
{
    void onNodeAdded(ObjectNode node);

    void onNodeRemoved(ObjectNode node);
}
