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
with Diagram('demoguiArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctxguitest', graph_attr=nodeattr):
          guiserver=Custom('guiserver','./qakicons/symActorSmall.png')
     sys >> Edge(color='red', style='dashed', xlabel='update_tt_state') >> guiserver
     sys >> Edge(color='red', style='dashed', xlabel='update_position') >> guiserver
     sys >> Edge(color='red', style='dashed', xlabel='update_container') >> guiserver
     sys >> Edge(color='red', style='dashed', xlabel='update_led') >> guiserver
diag
