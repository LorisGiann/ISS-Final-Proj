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
          alarmreceiverpickupdropdown=Custom('alarmreceiverpickupdropdown','./qakicons/symActorSmall.png')
          pickupdropouthandler=Custom('pickupdropouthandler','./qakicons/symActorSmall.png')
          mover=Custom('mover','./qakicons/symActorSmall.png')
          moveruturn=Custom('moveruturn','./qakicons/symActorSmall.png')
          alarmreceiverbasicrobot=Custom('alarmreceiverbasicrobot','./qakicons/symActorSmall.png')
          basicrobotwrapper=Custom('basicrobotwrapper','./qakicons/symActorSmall.png')
          basicrobot=Custom('basicrobot','./qakicons/symActorSmall.png')
          distancefilter=Custom('distancefilter(coded)','./qakicons/codedQActor.png')
     with Cluster('ctxalarm', graph_attr=nodeattr):
          sonar=Custom('sonar','./qakicons/symActorSmall.png')
          alarmemitter=Custom('alarmemitter','./qakicons/symActorSmall.png')
          led=Custom('led','./qakicons/symActorSmall.png')
          sonarsimulator=Custom('sonarsimulator(coded)','./qakicons/codedQActor.png')
          sonardatasource=Custom('sonardatasource(coded)','./qakicons/codedQActor.png')
          datacleaner=Custom('datacleaner(coded)','./qakicons/codedQActor.png')
          robotalarmcontrol=Custom('robotalarmcontrol(coded)','./qakicons/codedQActor.png')
          ledalarmcontrol=Custom('ledalarmcontrol(coded)','./qakicons/codedQActor.png')
     wasteservice >> Edge(color='magenta', style='solid', xlabel='depositaction') >> depositaction
     depositaction >> Edge(color='blue', style='solid', xlabel='err') >> wasteservice
     depositaction >> Edge(color='magenta', style='solid', xlabel='move') >> transporttrolley
     depositaction >> Edge(color='magenta', style='solid', xlabel='pickup') >> transporttrolley
     depositaction >> Edge(color='magenta', style='solid', xlabel='dropout') >> transporttrolley
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='pickup') >> pickupdropouthandler
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='dropout') >> pickupdropouthandler
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='move') >> mover
     alarmreceiverpickupdropdown >> Edge(color='blue', style='solid', xlabel='alarm') >> pickupdropouthandler
     alarmreceiverpickupdropdown >> Edge(color='blue', style='solid', xlabel='alarmceased') >> pickupdropouthandler
     sys >> Edge(color='red', style='dashed', xlabel='alarm') >> alarmreceiverpickupdropdown
     sys >> Edge(color='red', style='dashed', xlabel='alarmceased') >> alarmreceiverpickupdropdown
     mover >> Edge(color='magenta', style='solid', xlabel='cmdsync') >> basicrobotwrapper
     mover >> Edge(color='magenta', style='solid', xlabel='moveruturn') >> moveruturn
     moveruturn >> Edge(color='magenta', style='solid', xlabel='cmdsync') >> basicrobotwrapper
     alarmreceiverbasicrobot >> Edge(color='blue', style='solid', xlabel='alarm') >> basicrobotwrapper
     alarmreceiverbasicrobot >> Edge(color='blue', style='solid', xlabel='alarmceased') >> basicrobotwrapper
     sys >> Edge(color='red', style='dashed', xlabel='alarm') >> alarmreceiverbasicrobot
     sys >> Edge(color='red', style='dashed', xlabel='alarmceased') >> alarmreceiverbasicrobot
     basicrobotwrapper >> Edge(color='blue', style='solid', xlabel='cmd') >> basicrobot
     sys >> Edge(color='red', style='dashed', xlabel='info') >> basicrobotwrapper
     basicrobot >> Edge( xlabel='info', **eventedgeattr) >> sys
     sonar >> Edge(color='blue', style='solid', xlabel='sonaractivate') >> sonarsimulator
     sonar >> Edge(color='blue', style='solid', xlabel='sonaractivate') >> sonardatasource
     sys >> Edge(color='red', style='dashed', xlabel='alarmsonar') >> sonar
     sonar >> Edge(color='blue', style='solid', xlabel='sonardeactivate') >> sonarsimulator
     sonar >> Edge(color='blue', style='solid', xlabel='sonardeactivate') >> sonardatasource
     sonar >> Edge( xlabel='local_sonardata', **eventedgeattr) >> sys
     alarmemitter >> Edge( xlabel='alarm', **eventedgeattr) >> sys
     alarmemitter >> Edge( xlabel='alarmceased', **eventedgeattr) >> sys
     sys >> Edge(color='red', style='dashed', xlabel='local_sonardata') >> alarmemitter
     sys >> Edge(color='red', style='dashed', xlabel='update_led') >> led
diag
