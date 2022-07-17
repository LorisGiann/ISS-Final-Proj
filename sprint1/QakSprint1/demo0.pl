%====================================================================================
% demo0 description   
%====================================================================================
context(ctxserver, "localhost",  "TCP", "8095").
 qactor( wasteservice, ctxserver, "it.unibo.wasteservice.Wasteservice").
  qactor( transporttrolley, ctxserver, "it.unibo.transporttrolley.Transporttrolley").
  qactor( ddrrobot, ctxserver, "it.unibo.ddrrobot.Ddrrobot").
