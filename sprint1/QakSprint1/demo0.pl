%====================================================================================
% demo0 description   
%====================================================================================
context(ctxrpi, "localhost",  "TCP", "8095").
 qactor( wasteservice, ctxrpi, "it.unibo.wasteservice.Wasteservice").
  qactor( led, ctxrpi, "it.unibo.led.Led").
  qactor( testwasteservice, ctxrpi, "it.unibo.testwasteservice.Testwasteservice").
  qactor( transporttrolley, ctxrpi, "it.unibo.transporttrolley.Transporttrolley").
