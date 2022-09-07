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
          basicrobotlorisdavide=Custom('basicrobotlorisdavide','./qakicons/symActorSmall.png')
          commandissuerfortests=Custom('commandissuerfortests','./qakicons/symActorSmall.png')
          alarmreceivertest=Custom('alarmreceivertest','./qakicons/symActorSmall.png')
          distancefilter=Custom('distancefilter(coded)','./qakicons/codedQActor.png')
          transporttrolleystate=Custom('transporttrolleystate(coded)','./qakicons/codedQActor.png')
     with Cluster('ctxalarm', graph_attr=nodeattr):
          sonarlorisdavide=Custom('sonarlorisdavide','./qakicons/symActorSmall.png')
          alarmemitter=Custom('alarmemitter','./qakicons/symActorSmall.png')
          led=Custom('led','./qakicons/symActorSmall.png')
          sonarsimulator=Custom('sonarsimulator(coded)','./qakicons/codedQActor.png')
          sonardatasource=Custom('sonardatasource(coded)','./qakicons/codedQActor.png')
          datacleaner=Custom('datacleaner(coded)','./qakicons/codedQActor.png')
          ledalarmcontrol=Custom('ledalarmcontrol(coded)','./qakicons/codedQActor.png')
     wasteservice >> Edge(color='magenta', style='solid', xlabel='depositaction', fontcolor='magenta') >> depositaction
     depositaction >> Edge(color='blue', style='solid', xlabel='err', fontcolor='blue') >> wasteservice
     depositaction >> Edge(color='magenta', style='solid', xlabel='moveto', fontcolor='magenta') >> transporttrolley
     depositaction >> Edge(color='magenta', style='solid', xlabel='pickup', fontcolor='magenta') >> transporttrolley
     depositaction >> Edge(color='magenta', style='solid', xlabel='dropout', fontcolor='magenta') >> transporttrolley
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='pickup', fontcolor='magenta') >> pickupdropouthandler
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='dropout', fontcolor='magenta') >> pickupdropouthandler
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='moveto', fontcolor='magenta') >> mover
     alarmreceiverpickupdropdown >> Edge(color='blue', style='solid', xlabel='alarm', fontcolor='blue') >> pickupdropouthandler
     alarmreceiverpickupdropdown >> Edge(color='blue', style='solid', xlabel='alarmceased', fontcolor='blue') >> pickupdropouthandler
     sys >> Edge(color='red', style='dashed', xlabel='alarm', fontcolor='red') >> alarmreceiverpickupdropdown
     sys >> Edge(color='red', style='dashed', xlabel='alarmceased', fontcolor='red') >> alarmreceiverpickupdropdown
     mover >> Edge(color='magenta', style='solid', xlabel='cmdsync', fontcolor='magenta') >> basicrobotwrapper
     mover >> Edge(color='magenta', style='solid', xlabel='moveruturn', fontcolor='magenta') >> moveruturn
     moveruturn >> Edge(color='magenta', style='solid', xlabel='cmdsync', fontcolor='magenta') >> basicrobotwrapper
     alarmreceiverbasicrobot >> Edge(color='blue', style='solid', xlabel='alarm', fontcolor='blue') >> basicrobotwrapper
     alarmreceiverbasicrobot >> Edge(color='blue', style='solid', xlabel='alarmceased', fontcolor='blue') >> basicrobotwrapper
     sys >> Edge(color='red', style='dashed', xlabel='alarm', fontcolor='red') >> alarmreceiverbasicrobot
     sys >> Edge(color='red', style='dashed', xlabel='alarmceased', fontcolor='red') >> alarmreceiverbasicrobot
     basicrobotwrapper >> Edge(color='blue', style='solid', xlabel='cmd', fontcolor='blue') >> basicrobotlorisdavide
     sys >> Edge(color='red', style='dashed', xlabel='info', fontcolor='red') >> basicrobotwrapper
     basicrobotlorisdavide >> Edge( xlabel='info', **eventedgeattr, fontcolor='red') >> sys
     sonarlorisdavide >> Edge(color='blue', style='solid', xlabel='sonaractivate', fontcolor='blue') >> sonarsimulator
     sonarlorisdavide >> Edge(color='blue', style='solid', xlabel='sonaractivate', fontcolor='blue') >> sonardatasource
     sys >> Edge(color='red', style='dashed', xlabel='alarmsonar', fontcolor='red') >> sonarlorisdavide
     sonarlorisdavide >> Edge(color='blue', style='solid', xlabel='sonardeactivate', fontcolor='blue') >> sonarsimulator
     sonarlorisdavide >> Edge(color='blue', style='solid', xlabel='sonardeactivate', fontcolor='blue') >> sonardatasource
     sonarlorisdavide >> Edge( xlabel='local_sonardata', **eventedgeattr, fontcolor='red') >> sys
     alarmemitter >> Edge( xlabel='alarm', **eventedgeattr, fontcolor='red') >> sys
     alarmemitter >> Edge( xlabel='alarmceased', **eventedgeattr, fontcolor='red') >> sys
     sys >> Edge(color='red', style='dashed', xlabel='local_sonardata', fontcolor='red') >> alarmemitter
     sys >> Edge(color='red', style='dashed', xlabel='update_led', fontcolor='red') >> led
     commandissuerfortests >> Edge(color='magenta', style='solid', xlabel='pickup', fontcolor='magenta') >> pickupdropouthandler
     commandissuerfortests >> Edge(color='magenta', style='solid', xlabel='dropout', fontcolor='magenta') >> pickupdropouthandler
     commandissuerfortests >> Edge(color='magenta', style='solid', xlabel='cmdsync', fontcolor='magenta') >> basicrobotwrapper
     sys >> Edge(color='red', style='dashed', xlabel='alarm', fontcolor='red') >> alarmreceivertest
     sys >> Edge(color='red', style='dashed', xlabel='alarmceased', fontcolor='red') >> alarmreceivertest
diag
