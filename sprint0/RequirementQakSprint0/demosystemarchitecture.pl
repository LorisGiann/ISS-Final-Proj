%====================================================================================
% demosystemarchitecture description   
%====================================================================================
context(ctxdriver, "localhost",  "TCP", "8094").
context(ctxserver, "localhost",  "TCP", "8095").
context(ctxrobot, "localhost",  "TCP", "8096").
context(ctxalarm, "localhost",  "TCP", "8097").
 qactor( smartdevice, ctxdriver, "it.unibo.smartdevice.Smartdevice").
  qactor( wasteservice, ctxserver, "it.unibo.wasteservice.Wasteservice").
  qactor( transporttrolley, ctxrobot, "it.unibo.transporttrolley.Transporttrolley").
  qactor( basicrobot, ctxrobot, "it.unibo.basicrobot.Basicrobot").
  qactor( guiserver, ctxserver, "it.unibo.guiserver.Guiserver").
  qactor( led, ctxalarm, "it.unibo.led.Led").
  qactor( sonar, ctxalarm, "it.unibo.sonar.Sonar").
