%====================================================================================
% demobasicrobot description   
%====================================================================================
context(ctxrobottest, "localhost",  "TCP", "8095").
 qactor( distancefilter, ctxrobottest, "rx.distanceFilter").
  qactor( basicrobot, ctxrobottest, "it.unibo.basicrobot.Basicrobot").
