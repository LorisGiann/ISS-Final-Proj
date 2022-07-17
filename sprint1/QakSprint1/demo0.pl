%====================================================================================
% demo0 description   
%====================================================================================
context(ctxserver, "localhost",  "TCP", "8095").
 qactor( distancefilter, ctxserver, "rx.distanceFilter").
  qactor( wasteservice, ctxserver, "it.unibo.wasteservice.Wasteservice").
  qactor( transporttrolley, ctxserver, "it.unibo.transporttrolley.Transporttrolley").
  qactor( basicrobot, ctxserver, "it.unibo.basicrobot.Basicrobot").
