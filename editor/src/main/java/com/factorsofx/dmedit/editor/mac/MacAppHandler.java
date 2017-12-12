package com.factorsofx.dmedit.editor.mac;

import com.apple.eawt.Application;

public class MacAppHandler
{
    public static void setup()
    {
        Application app = Application.getApplication();
        app.setQuitHandler((event, resp) ->
        {
            resp.performQuit();
        });
    }
}
