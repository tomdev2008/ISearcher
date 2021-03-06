
package net.ion.crawler.core;

import java.io.IOException;
import java.util.Collection;
import java.util.EventListener;

import net.ion.crawler.auth.IAuth;
import net.ion.crawler.event.LoadingEvent;
import net.ion.crawler.event.ParserEvent;
import net.ion.crawler.filter.ILinkFilter;
import net.ion.crawler.handler.HTMLDocumentHandler;
import net.ion.crawler.link.Link;
import net.ion.crawler.model.ICrawlerModel;
import net.ion.crawler.parser.IParser;
import net.ion.crawler.parser.PageData;
import net.ion.crawler.parser.httpclient.SimpleHttpClientParser;
import net.ion.nsearcher.index.collect.AbstractCollector;
import net.ion.nsearcher.index.collect.ICollector;
import net.ion.nsearcher.index.event.ILoadingEventListener;
import net.ion.nsearcher.index.event.IParserEventListener;
import net.ion.nsearcher.index.handler.DocumentHandler;

public abstract class AbstractCrawler extends AbstractCollector implements ICrawler, ICollector {

    private IParser parser;

    private ICrawlerModel model;

    private ILinkFilter linkFilter = new ILinkFilter() {
		
		public boolean accept(Link link) {
			return true;
		}
	};
    
    private IAuth auth ;
    private DocumentHandler handler ;
    
    public AbstractCrawler(){
    	this(DEFAULT_NAME + "/CRAWL") ;
    }

    public AbstractCrawler(String name) {
		super(name) ;
		this.handler = new HTMLDocumentHandler() ;
	}

    public void setDocumentHandler(DocumentHandler handler){
    	this.handler = handler ;
    }
 
    public IParser getParser() {
        return parser;
    }

    public void setParser(IParser parser) {
        this.parser = parser;
    }

    public ICrawlerModel getModel() {
        return model;
    }

    public void setModel(ICrawlerModel model) {
        this.model = model;
    }

    public ILinkFilter getLinkFilter() {
        return linkFilter;
    }

    public void setLinkFilter(ILinkFilter linkFilter) {
        this.linkFilter = linkFilter;
    }
    
    
    public void setAuth(IAuth auth){
   		this.auth = auth ;
    }
    
    protected IAuth getAuth(){
    	return this.auth ;
    }
    
    protected void authProgress(IParser parser) throws IOException{
    	if (this.auth == null) return ;
    	
    	if (parser instanceof SimpleHttpClientParser){
    		((SimpleHttpClientParser)parser).authProgress(this.auth) ; 
    	}
    		
    }
    
    public void addLoadingListener(ILoadingEventListener listener) {
    	addListener(listener) ;
    }

    public void removeLoadingListener(ILoadingEventListener listener) {
    	removeListener(listener);
    }

    public void addParserListener(IParserEventListener listener) {
    	addListener(listener) ;
    }

    public void removeParserListener(IParserEventListener listener) {
    	removeListener(listener);
    }

    protected void fireBeforeLoadingEvent(Link link) {
        if (getEventListenerList().length == 0) {
            return;
        }

        // create the event
        LoadingEvent event = new LoadingEvent(this, link, null, 0L);

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (EventListener listener : getEventListenerList()) {
            if (listener instanceof ILoadingEventListener){
            	((ILoadingEventListener)listener).beforeLoading(event);
            }
        }
        

    }

    protected void fireAfterLoadingEvent(Link link, PageData pageData, long loadTime) {
        if (getEventListenerList().length == 0) {
            return;
        }

        // create the event
        LoadingEvent event = new LoadingEvent(this, link, pageData, loadTime);

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (EventListener listener : getEventListenerList()) {
            if (listener instanceof ILoadingEventListener){
            	((ILoadingEventListener)listener).afterLoading(event);
            }
        }
    }

    protected void fireParserEvent(Link link, PageData pageData, Collection<Link> outgoingLinks, long parseTime) {
        if (getEventListenerList().length == 0) {
            return;
        }

        // create the event
        ParserEvent event = ParserEvent.create(this, link, pageData, outgoingLinks, parseTime);

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (EventListener listener : getEventListenerList()) {
            if (listener instanceof IParserEventListener){
            	((IParserEventListener)listener).parsed(event);
            }
        }

    }

    protected boolean isPageDataOK(PageData pageData) {
        switch (pageData.getStatus()) {
            case PageData.OK:
            case PageData.NOT_MODIFIED:
            case PageData.REDIRECT:
                return true;
            default:
                return false;
        }
    }

    public DocumentHandler getDocumentHandler(){
    	return handler ;
    }

}
