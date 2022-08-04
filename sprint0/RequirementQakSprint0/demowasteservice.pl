%====================================================================================
% demowasteservice description   
%====================================================================================
context(ctxserver, "localhost",  "TCP", "8095").
 qactor( wasteservice, ctxserver, "it.unibo.wasteservice.Wasteservice").
