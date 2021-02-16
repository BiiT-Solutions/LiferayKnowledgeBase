package com.biit.liferay.access;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.biit.liferay.access.exceptions.ArticleNotDeletedException;
import com.biit.liferay.access.exceptions.ArticleNotFoundException;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.log.LiferayClientLogger;
import com.biit.liferay.model.IArticle;
import com.biit.liferay.model.KbArticle;
import com.biit.liferay.model.KbFolder;
import com.biit.usermanager.entity.IElement;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Named
public class ArticleService extends ServiceAccess<IArticle<Long>, KbArticle> implements IArticleService {
    private final static long ARTICLE_PARENT_RESOURCE_PRIMKEY = 0l;
    private final static long ARTICLE_PARENT_RESOURCE_CLASSNAME = 41603l;

    private final static String DIR_NAME = "";

    private SiteService siteService;
    private CompanyService companyService;
    private ClassNameService classNameService;

    public ArticleService() {
        super();
        reset();
    }

    @Override
    public void reset() {
        ArticlePool.getInstance().reset();
    }

    @Override
    public void disconnect() {
        super.disconnect();
        siteService.disconnect();
        companyService.disconnect();
        classNameService.disconnect();
    }

    @Override
    public void authorizedServerConnection(String address, String protocol, int port, String proxyPrefix, String webservicesPath,
                                           String authenticationToken, String loginUser, String password) {
        // Standard behavior.
        super.authorizedServerConnection(address, protocol, port, proxyPrefix, webservicesPath, authenticationToken, loginUser,
                password);
        // Disconnect previous connections.
        try {
            siteService.disconnect();
        } catch (Exception e) {

        }
        try {
            companyService.disconnect();
        } catch (Exception e) {

        }
        try {
            classNameService.disconnect();
        } catch (Exception e) {

        }
        // Sites are needed for some services.
        siteService = new SiteService();
        siteService.authorizedServerConnection(address, protocol, port, proxyPrefix, webservicesPath, authenticationToken, loginUser,
                password);
        // Sites are needed for some services.
        companyService = new CompanyService();
        companyService.authorizedServerConnection(address, protocol, port, proxyPrefix, webservicesPath, authenticationToken,
                loginUser, password);
        // classNames are needed to add an article.
        classNameService = new ClassNameService();
        classNameService.authorizedServerConnection(address, protocol, port, proxyPrefix, webservicesPath, authenticationToken,
                loginUser, password);
    }

    @Override
    public Set<IArticle<Long>> decodeListFromJson(String json, Class<KbArticle> objectClass)
            throws IOException {
        Set<IArticle<Long>> myObjects = new ObjectMapper().readValue(json, new TypeReference<Set<KbArticle>>() {
        });
        return myObjects;
    }

    /**
     * Gets latest published articled.
     *
     * @param resourcePrimKey
     * @return
     * @throws NotConnectedToWebServiceException
     * @throws ClientProtocolException
     * @throws IOException
     * @throws AuthenticationRequired
     * @throws WebServiceAccessError
     * @throws ArticleNotFoundException
     */
    @Override
    public IArticle<Long> getLatestArticle(long resourcePrimKey)
            throws NotConnectedToWebServiceException, IOException, AuthenticationRequired,
            WebServiceAccessError, ArticleNotFoundException {
        return getLatestArticle(resourcePrimKey, 0);
    }

    /**
     * Gets an article by its resource key and the status.
     *
     * @param resourcePrimKey
     * @param status      0 if published.
     * @return
     * @throws NotConnectedToWebServiceException
     * @throws ClientProtocolException
     * @throws IOException
     * @throws AuthenticationRequired
     * @throws WebServiceAccessError
     * @throws ArticleNotFoundException
     */
    @Override
    public IArticle<Long> getLatestArticle(long resourcePrimKey, int status)
            throws NotConnectedToWebServiceException, IOException, AuthenticationRequired,
            WebServiceAccessError, ArticleNotFoundException {

        IArticle<Long> article = ArticlePool.getInstance().getElement(resourcePrimKey);
        if (article != null) {
            return article;
        }
        checkConnection();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("resourcePrimKey", Long.toString(resourcePrimKey)));
        params.add(new BasicNameValuePair("status", Integer.toString(status)));

        String result = getHttpPostResponse("knowledge-base-portlet.kbarticle/get-latest-kb-article", params);

        LiferayClientLogger.debug(this.getClass().getName(), "Data retrieved: '" + result + "'.");

        if (result != null) {
            // Check some errors
            if (result.contains("NoSuchArticleException")) {
                throw new ArticleNotFoundException("Article with key '" + resourcePrimKey + "' not found on Liferay.");
            }
            // A Simple JSON Response Read
            article = decodeFromJson(result, KbArticle.class);
            ArticlePool.getInstance().addElement(article);
            return article;
        }
        return null;
    }

    @Override
    public IArticle<Long> addArticle(IArticle<Long> article, IGroup<Long> site) throws ClientProtocolException,
            NotConnectedToWebServiceException, IOException, AuthenticationRequired, WebServiceAccessError {
        Long parentResourcePrimKey = 0l;
        Long parentResourceClassId = 0l;
        String urlTitle = "";
        String sourceURL = "";
        List<String> selectedFileNames = null;
        if (article instanceof KbArticle) {
            parentResourcePrimKey = ((KbArticle) article).getParentResourcePrimKey();
            parentResourceClassId = ((KbArticle) article).getParentResourceClassNameId();
            urlTitle = ((KbArticle) article).getUrlTitle();
            sourceURL = ((KbArticle) article).getSourceURL();
            selectedFileNames = ((KbArticle) article).getSelectedFileNames();
        }
        return addArticle(parentResourcePrimKey, parentResourceClassId, article.getTitle(), urlTitle,
                article.getContent(), article.getDescription(), sourceURL, article.getSections(), selectedFileNames,
                site);
    }

    @Override
    public IArticle<Long> addArticle(IArticle<Long> article, String siteName, String virtualHost)
            throws ClientProtocolException, NotConnectedToWebServiceException, IOException, AuthenticationRequired,
            WebServiceAccessError {
        Long parentResourcePrimKey = 0l;
        Long parentResourceClassId = 0l;
        String urlTitle = "";
        String sourceURL = "";
        List<String> selectedFileNames = null;
        if (article instanceof KbArticle) {
            parentResourcePrimKey = ((KbArticle) article).getParentResourcePrimKey();
            parentResourceClassId = ((KbArticle) article).getParentResourceClassNameId();
            urlTitle = ((KbArticle) article).getUrlTitle();
            sourceURL = ((KbArticle) article).getSourceURL();
            selectedFileNames = ((KbArticle) article).getSelectedFileNames();
        }
        return addArticle(parentResourcePrimKey, parentResourceClassId, article.getTitle(), urlTitle,
                article.getContent(), article.getDescription(), sourceURL, article.getSections(), selectedFileNames,
                siteName, virtualHost);
    }

    @Override
    public IArticle<Long> addArticle(Long parentResourcePrimKey, Long parentResourceClassNameId, String title,
                                     String urlTitle, String content, String description, String sourceURL, List<String> sections,
                                     List<String> selectedFileNames, String siteName, String virtualHost)
            throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired,
            WebServiceAccessError {
        checkConnection();

        IGroup<Long> company = companyService.getCompanyByVirtualHost(virtualHost);
        IGroup<Long> site = siteService.getSite(company, siteName);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("portletId", PortletId.KNOWLEDGEBASE_PORTLET.getId()));
        if (parentResourcePrimKey != null) {
            params.add(new BasicNameValuePair("parentResourcePrimKey", Long.toString(parentResourcePrimKey)));
        } else {
            params.add(new BasicNameValuePair("parentResourcePrimKey", Long.toString(ARTICLE_PARENT_RESOURCE_PRIMKEY)));
        }
        if (parentResourceClassNameId != null) {
            params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(parentResourceClassNameId)));
        } else {
            // get className id from another webservice.
            IElement<Long> className = classNameService.getClassName(KbFolder.FOLDER_LIFERAY_CLASSNAME);
            if (className != null) {
                params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(className.getUniqueId())));
            } else {
                params.add(new BasicNameValuePair("parentResourceClassNameId",
                        Long.toString(ARTICLE_PARENT_RESOURCE_CLASSNAME)));
            }
        }
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("urlTitle", urlTitle));
        params.add(new BasicNameValuePair("content", content));
        params.add(new BasicNameValuePair("description", description));
        params.add(new BasicNameValuePair("sourceURL", sourceURL));
        params.add(new BasicNameValuePair("sections", convertToJson(sections)));
        params.add(new BasicNameValuePair("selectedFileNames", convertToJson(selectedFileNames)));
        params.add(new BasicNameValuePair("serviceContext.scopeGroupId", Long.toString(site.getUniqueId())));

        String result = getHttpPostResponse("knowledge-base-portlet.kbarticle/add-kb-article", params);

        if (result != null) {
            // A Simple JSON Response Read
            IArticle<Long> article = decodeFromJson(result, KbArticle.class);
            ArticlePool.getInstance().addElement(article);
            return article;
        }
        return null;
    }

    @Override
    public IArticle<Long> addArticle(Long parentResourcePrimKey, Long parentResourceClassNameId, String title,
                                     String urlTitle, String content, String description, String sourceURL, List<String> sections,
                                     List<String> selectedFileNames, IGroup<Long> site) throws NotConnectedToWebServiceException,
            ClientProtocolException, IOException, AuthenticationRequired, WebServiceAccessError {
        checkConnection();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("portletId", PortletId.KNOWLEDGEBASE_PORTLET.getId()));
        if (parentResourcePrimKey != null) {
            params.add(new BasicNameValuePair("parentResourcePrimKey", Long.toString(parentResourcePrimKey)));
        } else {
            params.add(new BasicNameValuePair("parentResourcePrimKey", Long.toString(ARTICLE_PARENT_RESOURCE_PRIMKEY)));
        }
        if (parentResourceClassNameId != null) {
            params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(parentResourceClassNameId)));
        } else {
            // get className id from another webservice.
            IElement<Long> className = classNameService.getClassName(KbFolder.FOLDER_LIFERAY_CLASSNAME);
            if (className != null) {
                params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(className.getUniqueId())));
            } else {
                params.add(new BasicNameValuePair("parentResourceClassNameId",
                        Long.toString(ARTICLE_PARENT_RESOURCE_CLASSNAME)));
            }
        }
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("urlTitle", urlTitle));
        params.add(new BasicNameValuePair("content", content));
        params.add(new BasicNameValuePair("description", description));
        params.add(new BasicNameValuePair("sourceURL", sourceURL));
        params.add(new BasicNameValuePair("sections", convertToJson(sections)));
        params.add(new BasicNameValuePair("selectedFileNames", convertToJson(selectedFileNames)));
        params.add(new BasicNameValuePair("serviceContext.scopeGroupId", Long.toString(site.getUniqueId())));

        String result = getHttpPostResponse("knowledge-base-portlet.kbarticle/add-kb-article", params);

        if (result != null) {
            // A Simple JSON Response Read
            IArticle<Long> article = decodeFromJson(result, KbArticle.class);
            ArticlePool.getInstance().addElement(article);
            return article;
        }
        return null;
    }

    @Override
    public Integer getArticlesCount(IGroup<Long> site)
            throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired {
        checkConnection();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("groupId", Long.toString(site.getUniqueId())));
        params.add(new BasicNameValuePair("status", "0"));

        String result = getHttpPostResponse("/knowledge-base-portlet.kbarticle/get-group-kb-articles-count", params);

        if (result != null) {
            try {
                return Integer.parseInt(result);
            } catch (NumberFormatException nfe) {
                LiferayClientLogger.errorMessage(this.getClass().getName(), nfe);
            }
        }
        return null;
    }

    @Override
    public Set<IArticle<Long>> getArticles(IGroup<Long> site)
            throws ClientProtocolException, NotConnectedToWebServiceException, IOException, AuthenticationRequired {
        return getArticles(site, 0, 1000);
    }

    @Override
    public Set<IArticle<Long>> getArticles(IGroup<Long> site, int start, int end)
            throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired {
        checkConnection();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("groupId", Long.toString(site.getUniqueId())));
        params.add(new BasicNameValuePair("status", "0"));
        params.add(new BasicNameValuePair("start", Integer.toString(start)));
        params.add(new BasicNameValuePair("end", Integer.toString(end)));
        params.add(new BasicNameValuePair("-orderByComparator", null));

        String result = getHttpPostResponse("/knowledge-base-portlet.kbarticle/get-group-kb-articles", params);

        if (result != null) {
            // A Simple JSON Response Read
            Set<IArticle<Long>> articles = decodeListFromJson(result, KbArticle.class);
            return articles;
        }
        return new HashSet<>();
    }

    @Override
    public IArticle<Long> deleteArticle(IArticle<Long> article)
            throws NotConnectedToWebServiceException, ClientProtocolException, IOException, AuthenticationRequired,
            ArticleNotDeletedException, WebServiceAccessError {
        if (article != null) {
            checkConnection();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("resourcePrimKey", article.getResourcePrimKey() + ""));

            String result = getHttpPostResponse("knowledge-base-portlet.kbarticle/delete-kb-article", params);
            if (result != null) {
                // A Simple JSON Response Read
                IArticle<Long> articleDeleted = decodeFromJson(result, KbArticle.class);
                ArticlePool.getInstance().addElement(articleDeleted);
                LiferayClientLogger.info(this.getClass().getName(),
                        "Article '" + articleDeleted.getTitle() + "' deleted.");
                return articleDeleted;
            } else {
                throw new ArticleNotDeletedException("Article '" + article.getTitle() + "' (id:" + article.getUniqueId()
                        + ") not deleted correctly. ");
            }
        }
        return null;
    }

    @Override
    public IArticle<Long> editArticle(IArticle<Long> article) throws NotConnectedToWebServiceException,
            ClientProtocolException, IOException, AuthenticationRequired, WebServiceAccessError {
        if (article != null) {
            checkConnection();

            List<String> selectedFileNames = null;
            String sourceURL = "";
            if (article instanceof KbArticle) {
                sourceURL = ((KbArticle) article).getSourceURL();
                selectedFileNames = ((KbArticle) article).getSelectedFileNames();
            }

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if (article.getResourcePrimKey() != null) {
                params.add(new BasicNameValuePair("resourcePrimKey", Long.toString(article.getResourcePrimKey())));
            } else {
                params.add(new BasicNameValuePair("resourcePrimKey", Long.toString(ARTICLE_PARENT_RESOURCE_PRIMKEY)));
            }
            params.add(new BasicNameValuePair("title", article.getTitle()));
            params.add(new BasicNameValuePair("content", article.getContent()));
            params.add(new BasicNameValuePair("description", article.getDescription()));
            params.add(new BasicNameValuePair("sourceURL", sourceURL));
            params.add(new BasicNameValuePair("sections", convertToJson(article.getSections())));
            params.add(new BasicNameValuePair("selectedFileNames", convertToJson(selectedFileNames)));
            params.add(new BasicNameValuePair("removeFileEntryIds", convertToJson(new ArrayList<String>())));
            params.add(new BasicNameValuePair("dirName", DIR_NAME));

            String result = getHttpPostResponse("knowledge-base-portlet.kbarticle/update-kb-article", params);

            if (result != null) {
                // A Simple JSON Response Read
                IArticle<Long> articleUpdated = decodeFromJson(result, KbArticle.class);
                ArticlePool.getInstance().addElement(articleUpdated);
                return articleUpdated;
            }
        }
        return null;
    }

    @Override
    public IArticle<Long> createArticle(String title, String content, String description, List<String> sections)
            throws ClientProtocolException, NotConnectedToWebServiceException, IOException, AuthenticationRequired,
            WebServiceAccessError {
        checkConnection();
        KbArticle article = new KbArticle();
        IElement<Long> className = classNameService.getClassName(KbFolder.FOLDER_LIFERAY_CLASSNAME);
        if (className != null) {
            article.setParentResourceClassNameId(className.getUniqueId());
            article.setParentResourcePrimKey(0l);
        }
        article.setTitle(title);
        article.setContent(content);
        article.setDescription(description);
        article.setSections(sections);

        return article;
    }

    @Override
    public void moveArticle(long resourcePrimKey, Long folderId) throws NotConnectedToWebServiceException,
            ClientProtocolException, IOException, AuthenticationRequired, WebServiceAccessError {
        checkConnection();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("resourcePrimKey", Long.toString(resourcePrimKey)));

        // get className id from another webservice.
        IElement<Long> className = classNameService.getClassName(ArticleFolderService.FOLDER_PARENT_CLASSNAME);
        if (className != null) {
            params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(className.getUniqueId())));
        } else {
            params.add(new BasicNameValuePair("parentResourceClassNameId", Long.toString(0)));
        }

        params.add(new BasicNameValuePair("parentResourcePrimKey", Long.toString(folderId)));
        params.add(new BasicNameValuePair("priority", "1.0"));

        LiferayClientLogger.debug(this.getClass().getName(),
                "Moving article '" + resourcePrimKey + "' with params " + params);
        String result = getHttpPostResponse("knowledge-base-portlet.kbarticle/move-kb-article", params);

        // A Simple JSON Response Read
        LiferayClientLogger.info(this.getClass().getName(), "Moving article '" + resourcePrimKey + "' to folder  '"
                + folderId + "' has as result '" + result + "'.");
    }

}
