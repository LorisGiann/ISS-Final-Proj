%====================================================================================
% demogui description   
%====================================================================================
context(ctxguitest, "localhost",  "TCP", "8095").
 qactor( guiserver, ctxguitest, "it.unibo.guiserver.Guiserver").
