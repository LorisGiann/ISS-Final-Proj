%====================================================================================
% demo0 description   
%====================================================================================
context(ctxrpi, "localhost",  "TCP", "8095").
 qactor( waste_service, ctxrpi, "it.unibo.waste_service.Waste_service").
  qactor( transporttrolley, ctxrpi, "it.unibo.transporttrolley.Transporttrolley").
  qactor( led, ctxrpi, "it.unibo.led.Led").
