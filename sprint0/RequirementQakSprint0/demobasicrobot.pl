%====================================================================================
% demobasicrobot description   
%====================================================================================
context(ctxrobot, "localhost",  "TCP", "8095").
 qactor( distancefilter, ctxrobot, "rx.distanceFilter").
  qactor( basicrobot, ctxrobot, "it.unibo.basicrobot.Basicrobot").
