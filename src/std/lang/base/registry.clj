(ns std.lang.base.registry)

(def +registry+
  (atom {[:postgres :default]          'rt.postgres.grammar
         [:postgres :jdbc]             'rt.postgres.client
         [:postgres :jdbc.client]      'rt.postgres.client
         
         [:solidity :default]          'rt.solidity.grammar
         #_#_#_#_
         [:solidity :truffle]          'rt.solidity.client
         [:solidity :truffle.shared]   'rt.solidity.client
	 
	 [:bash   :oneshot]            'rt.basic.impl.process-bash
	 [:bash   :basic]              'rt.shell
	 [:bash   :remote]             'rt.shell

         [:lua    :oneshot]            'rt.basic.impl.process-lua
         [:lua    :basic]              'rt.basic.impl.process-lua
         [:lua    :interactive]        'rt.basic.impl.process-lua
         [:lua    :websocket]          'rt.basic.impl.process-lua
         [:lua    :nginx]              'rt.nginx
         [:lua    :nginx.instance]     'rt.nginx
         [:lua    :redis]              'rt.redis
         [:lua    :remote-port]      'rt.basic.impl.remote-lua
         [:lua    :remote-ws]          'rt.basic.impl.remote-lua
         
         [:js     :oneshot]            'rt.basic.impl.process-js
         [:js     :basic]              'rt.basic.impl.process-js
         [:js     :interactive]        'rt.basic.impl.process-js
         [:js     :websocket]          'rt.basic.impl.process-js
         [:js     :javafx]             'rt.javafx
         [:js     :graal]              'rt.graal
         [:js     :browser]            'rt.browser
         [:js     :remote-port]      'rt.basic.impl.remote-js
         [:js     :remote-ws]          'rt.basic.impl.remote-js
         
         [:python :oneshot]            'rt.basic.impl.process-python
         [:python :basic]              'rt.basic.impl.process-python
         [:python :interactive]        'rt.basic.impl.process-python
         [:python :websocket]          'rt.basic.impl.process-python
         [:python :graal]              'rt.graal
         [:python :jep]                'rt.jep
         [:python :remote-port]      'rt.basic.impl.process-python
         [:python :remote-ws]          'rt.basic.impl.process-python

         [:r      :oneshot]            'rt.basic.impl.process-r
         [:r      :basic]              'rt.basic.impl.process-r
         
         [:c      :jocl]               'rt.jocl
         [:c      :oneshot]            'rt.basic.impl.process-c
	 
         [:xtalk  :oneshot]            'rt.basic.impl.process-xtalk}))
