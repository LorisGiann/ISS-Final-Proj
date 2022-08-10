from diagrams import Cluster, Diagram, Edge
from diagrams.custom import Custom
import os
os.environ['PATH'] += os.pathsep + 'C:/Program Files/Graphviz/bin/'

graphattr = {     #https://www.graphviz.org/doc/info/attrs.html
    'fontsize': '22',
}

nodeattr = {   
    'fontsize': '22',
    'bgcolor': 'lightyellow'
}

eventedgeattr = {
    'color': 'red',
    'style': 'dotted'
}
with Diagram('demo0Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctxserver', graph_attr=nodeattr):
          wasteservice=Custom('wasteservice','./qakicons/symActorSmall.png')
          depositaction=Custom('depositaction','./qakicons/symActorSmall.png')
     with Cluster('ctxrobot', graph_attr=nodeattr):
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
          pickupdropouthandler=Custom('pickupdropouthandler','./qakicons/symActorSmall.png')
          mover=Custom('mover','./qakicons/symActorSmall.png')
          moveruturn=Custom('moveruturn','./qakicons/symActorSmall.png')
          mover180turn=Custom('mover180turn','./qakicons/symActorSmall.png')
          basicrobotwrapper=Custom('basicrobotwrapper','./qakicons/symActorSmall.png')
          basicrobot=Custom('basicrobot','./qakicons/symActorSmall.png')
          distancefilter=Custom('distancefilter(coded)','./qakicons/codedQActor.png')
     wasteservice >> Edge(color='magenta', style='solid', xlabel='depositaction') >> depositaction
     depositaction >> Edge(color='blue', style='solid', xlabel='err') >> wasteservice
     depositaction >> Edge(color='magenta', style='solid', xlabel='move') >> transporttrolley
     depositaction >> Edge(color='magenta', style='solid', xlabel='pickup') >> transporttrolley
     depositaction >> Edge(color='magenta', style='solid', xlabel='move') >> transporttrolley
     depositaction >> Edge(color='magenta', style='solid', xlabel='dropout') >> transporttrolley
     depositaction >> Edge(color='magenta', style='solid', xlabel='move') >> transporttrolley
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='pickup') >> pickupdropouthandler
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='dropout') >> pickupdropouthandler
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='move') >> mover
     mover >> Edge(color='magenta', style='solid', xlabel='cmdsync') >> basicrobotwrapper
     mover >> Edge(color='magenta', style='solid', xlabel='cmdsync') >> basicrobotwrapper
     mover >> Edge(color='magenta', style='solid', xlabel='cmdsync') >> basicrobotwrapper
     mover >> Edge(color='magenta', style='solid', xlabel='cmdsync') >> basicrobotwrapper
     mover >> Edge(color='magenta', style='solid', xlabel='mover180turn') >> mover180turn
     mover >> Edge(color='magenta', style='solid', xlabel='moveruturn') >> moveruturn
     moveruturn >> Edge(color='magenta', style='solid', xlabel='cmdsync') >> basicrobotwrapper
     moveruturn >> Edge(color='magenta', style='solid', xlabel='mover180turn') >> mover180turn
     moveruturn >> Edge(color='magenta', style='solid', xlabel='cmdsync') >> basicrobotwrapper
     moveruturn >> Edge(color='magenta', style='solid', xlabel='mover180turn') >> mover180turn
     mover180turn >> Edge(color='magenta', style='solid', xlabel='cmdsync') >> basicrobotwrapper
     mover180turn >> Edge(color='magenta', style='solid', xlabel='cmdsync') >> basicrobotwrapper
     basicrobotwrapper >> Edge(color='blue', style='solid', xlabel='cmd') >> basicrobot
     basicrobotwrapper >> Edge(color='blue', style='solid', xlabel='cmd') >> basicrobot
     sys >> Edge(color='red', style='dashed', xlabel='info') >> basicrobotwrapper
     basicrobot >> Edge( xlabel='info', **eventedgeattr) >> sys
diag