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
with Diagram('demosystemarchitectureArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctxdriver', graph_attr=nodeattr):
          smartdevice=Custom('smartdevice','./qakicons/symActorSmall.png')
     with Cluster('ctxserver', graph_attr=nodeattr):
          wasteservice=Custom('wasteservice','./qakicons/symActorSmall.png')
     with Cluster('ctxrobot', graph_attr=nodeattr):
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
          basicrobot=Custom('basicrobot','./qakicons/symActorSmall.png')
     smartdevice >> Edge(color='magenta', style='solid', xlabel='depositrequest') >> wasteservice
     wasteservice >> Edge(color='magenta', style='solid', xlabel='pickup') >> transporttrolley
     wasteservice >> Edge(color='magenta', style='solid', xlabel='dropout') >> transporttrolley
     wasteservice >> Edge(color='magenta', style='solid', xlabel='movetoHOME') >> transporttrolley
     wasteservice >> Edge(color='magenta', style='solid', xlabel='movetoINDOOR') >> transporttrolley
     wasteservice >> Edge(color='magenta', style='solid', xlabel='movetoPLASTICBOX') >> transporttrolley
     wasteservice >> Edge(color='magenta', style='solid', xlabel='movetoGLASSBOX') >> transporttrolley
     transporttrolley >> Edge(color='blue', style='solid', xlabel='cmd') >> basicrobot
     sys >> Edge(color='red', style='dashed', xlabel='info') >> transporttrolley
     basicrobot >> Edge( xlabel='info', **eventedgeattr) >> sys
diag
