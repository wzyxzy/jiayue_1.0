
(function($,Edge,compId){var Composition=Edge.Composition,Symbol=Edge.Symbol;
//Edge symbol: 'stage'
(function(symbolName){})("stage");
//Edge symbol end:'stage'

//=========================================================

//Edge symbol: 'Spin'
(function(symbolName){Symbol.bindTimelineAction(compId,symbolName,"Default Timeline","complete",function(sym,e){sym.play(0);});
//Edge binding end
Symbol.bindElementAction(compId,symbolName,"${_Center}","click",function(sym,e){if(sym.isPlaying())
sym.stop();else
sym.play();});
//Edge binding end
})("Spin");
//Edge symbol end:'Spin'
})(jQuery,AdobeEdge,"EDGE-130892631");