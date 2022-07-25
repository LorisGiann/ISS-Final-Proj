%====================================================================================
% basicrobot description   
%====================================================================================
context(ctxbasicrobot, "localhost",  "TCP", "8020").
 qactor( distancefilter, ctxbasicrobot, "rx.distanceFilter").
  qactor( commander, ctxbasicrobot, "it.unibo.commander.Commander").
  qactor( basicrobot, ctxbasicrobot, "it.unibo.basicrobot.Basicrobot").
