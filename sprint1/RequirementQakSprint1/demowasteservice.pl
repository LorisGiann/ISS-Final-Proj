%====================================================================================
% demowasteservice description   
%====================================================================================
context(ctxwasteservice, "localhost",  "TCP", "8095").
 qactor( wasteservice, ctxwasteservice, "it.unibo.wasteservice.Wasteservice").
