%====================================================================================
% demowasteservice description   
%====================================================================================
context(ctxservertest, "localhost",  "TCP", "8095").
 qactor( wasteservice, ctxservertest, "it.unibo.wasteservice.Wasteservice").
