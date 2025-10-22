package com.biit.liferay.test;

/*-
 * #%L
 * Access to Liferay Knowledge Base
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.liferay.access.*;
import com.biit.liferay.access.exceptions.*;
import com.biit.liferay.model.IArticle;
import com.biit.liferay.model.IRepository;
import com.biit.liferay.model.KbArticle;
import com.biit.liferay.model.Repository;
import com.biit.usermanager.entity.IGroup;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.liferay.portal.model.Site;
import org.apache.http.client.ClientProtocolException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArticleTest {

    private static final String LOGIN_USER = "webservices@test.com";
    private static final String LOGIN_PASSWORD = "my-pass";
    private static final String HOST = "testing.test.com";

    private static final String ARTICLE_TITLE = "Title";
    private static final String ARTICLE_CONTENT = "Content";
    private static final String ARTICLE_DESCRIPTION = "Description";
    private static final List<String> ARTICLE_SECTIONS = new ArrayList<>();

    private static final String ARTICLE_TITLE_UPDATE = "New Title";
    private static final String ARTICLE_CONTENT_UPDATE = "Content extended";

    private static final String LIFERAY_PROTOCOL = "https";
    private static final int PORT = 443;
    private static final String PROXY_PREFIX = "liferay/";
    private static final String WEBSERVICES_PATH = "api/jsonws/";
    private static final String AUTHENTICATION_TOKEN = "11111111";
    private static final String COMPANY_VIRTUALHOST = "virtualhost.test.com";

    private static final String SITE_NAME = "siteWithArticles";
    private static final String SITE_DESCRIPTION = "This site is created with the automated testing.";
    private static final String SITE_URL = "/article-site";

    private static final String REPOSITORY_DESCRIPTION = "This is one repository";

    private IGroup<Long> company;
    private IGroup<Long> site;

    private IArticle<Long> article = null;
    private IRepository<Long> repository;

    private CompanyService companyService = new CompanyService();
    private SiteService siteService = new SiteService();
    private ArticleService knowledgeBaseService = new ArticleService();
    private FileRepositoryService repositoryService = new FileRepositoryService();

    @Test(groups = {"connection"})
    public void authorized() throws NotConnectedToWebServiceException,
            IOException, AuthenticationRequired, WebServiceAccessError {
        companyService.authorizedServerConnection(HOST, LIFERAY_PROTOCOL, PORT, PROXY_PREFIX, WEBSERVICES_PATH, AUTHENTICATION_TOKEN,
                LOGIN_USER, LOGIN_PASSWORD);
        companyService.getCompanyByVirtualHost(COMPANY_VIRTUALHOST);
    }

    @Test(groups = {"companyAccess"}, dependsOnGroups = {"connection"})
    public void companyAccess() throws NotConnectedToWebServiceException,
            IOException, AuthenticationRequired, WebServiceAccessError {
        company = companyService.getCompanyByVirtualHost(COMPANY_VIRTUALHOST);
        Assert.assertNotNull(company);
    }

    @Test(groups = {"siteAccess"}, dependsOnGroups = {"connection"})
    public void siteAccess() throws NotConnectedToWebServiceException,
            IOException, AuthenticationRequired, WebServiceAccessError {
        siteService.authorizedServerConnection(HOST, LIFERAY_PROTOCOL, PORT, PROXY_PREFIX, WEBSERVICES_PATH, AUTHENTICATION_TOKEN,
                LOGIN_USER, LOGIN_PASSWORD);
        Assert.assertFalse(siteService.isNotConnected());
    }

    @Test(groups = {"siteAccess"}, dependsOnMethods = {"siteAccess"})
    public void addSite() throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
            AuthenticationRequired, WebServiceAccessError, DuplicatedLiferayElement {
        site = (Site) siteService.addSite(SITE_NAME, SITE_DESCRIPTION, SiteType.DEFAULT_PARENT_GROUP_ID, SITE_URL);
        Assert.assertNotNull(site);
    }

    @Test(groups = {"repository"}, dependsOnGroups = {"companyAccess", "siteAccess"})
    public void connectToRepositoryService() {
        repositoryService.authorizedServerConnection(HOST, LIFERAY_PROTOCOL, PORT, PROXY_PREFIX, WEBSERVICES_PATH,
                AUTHENTICATION_TOKEN, LOGIN_USER, LOGIN_PASSWORD);
        Assert.assertFalse(repositoryService.isNotConnected());
    }

    @Test(groups = {"repository"}, dependsOnMethods = {"connectToRepositoryService"})
    public void addRepository() throws ClientProtocolException, NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError {
        repository = repositoryService.addRespository(site, FileRepositoryName.SITE.getName(), REPOSITORY_DESCRIPTION);
        Assert.assertNotNull(((Repository) repository).getUuid());
        Assert.assertNotNull(((Repository) repository).getUniqueId());

        // repositoryService.createDLFoldersOfRepository(repository, company,
        // site);
    }

    @Test(groups = {"repository"}, dependsOnMethods = {"addRepository"})
    public void getRepository() throws ClientProtocolException, NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError {
        IRepository<Long> storedRepository = repositoryService.getRespository(repository.getUniqueId());
        Assert.assertNotNull(storedRepository);
    }

    @Test(groups = {"articleAccess"}, dependsOnGroups = {"companyAccess", "siteAccess", "repository"})
    public void connectToKnowledgeBaseWebService() {
        knowledgeBaseService.authorizedServerConnection(HOST, LIFERAY_PROTOCOL, PORT, PROXY_PREFIX, WEBSERVICES_PATH,
                AUTHENTICATION_TOKEN, LOGIN_USER, LOGIN_PASSWORD);
        Assert.assertFalse(knowledgeBaseService.isNotConnected());
    }

    @Test(groups = {"articleAccess"}, dependsOnMethods = {"connectToKnowledgeBaseWebService"})
    public void addArticle() throws ClientProtocolException, NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError {
        KbArticle articleCreated = (KbArticle) knowledgeBaseService.createArticle(ARTICLE_TITLE, ARTICLE_CONTENT,
                ARTICLE_DESCRIPTION, ARTICLE_SECTIONS);
        Assert.assertEquals(0, (int) knowledgeBaseService.getArticlesCount(site));
        knowledgeBaseService.addArticle(articleCreated, site);
        Assert.assertEquals(1, (int) knowledgeBaseService.getArticlesCount(site));
        Assert.assertEquals(1, (int) knowledgeBaseService.getArticles(site).size());
        article = knowledgeBaseService.addArticle(articleCreated, site);
        Assert.assertEquals(2, (int) knowledgeBaseService.getArticlesCount(site));
        Assert.assertEquals(2, (int) knowledgeBaseService.getArticles(site).size());
        Assert.assertNotNull(this.article);
    }

    @Test(groups = {"articleAccess"}, dependsOnMethods = {"addArticle"})
    public void getArticle() throws ClientProtocolException, NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError, ArticleNotFoundException {
        Assert.assertNotNull(article);
        IArticle<Long> articleStored = knowledgeBaseService.getLatestArticle(article.getResourcePrimKey());
        Assert.assertNotNull(articleStored);
    }

    @Test(groups = {"articleAccess"}, dependsOnMethods = {"getArticle"})
    public void updateArticle() throws ClientProtocolException, NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError, ArticleNotDeletedException, ArticleNotFoundException {
        Assert.assertNotNull(article);
        IArticle<Long> articleStored = knowledgeBaseService.getLatestArticle(article.getResourcePrimKey());
        Assert.assertNotNull(articleStored);

        ((KbArticle) articleStored).setTitle(ARTICLE_TITLE_UPDATE);
        ((KbArticle) articleStored).setContent(ARTICLE_CONTENT_UPDATE);
        knowledgeBaseService.editArticle(articleStored);

        IArticle<Long> articleUpdated = knowledgeBaseService.getLatestArticle(article.getResourcePrimKey());
        Assert.assertNotNull(articleUpdated);
        Assert.assertEquals(ARTICLE_TITLE_UPDATE, articleUpdated.getTitle());
        Assert.assertEquals(ARTICLE_CONTENT_UPDATE, articleUpdated.getContent());
    }

    @Test(alwaysRun = true, groups = {"deleteData"}, dependsOnGroups = {"articleAccess"}, dependsOnMethods = {
            "updateArticle"}, expectedExceptions = ArticleNotFoundException.class)
    public void articleDelete() throws NotConnectedToWebServiceException, ClientProtocolException, IOException,
            AuthenticationRequired, ArticleNotDeletedException, WebServiceAccessError, ArticleNotFoundException {
        Assert.assertNotNull(article);
        knowledgeBaseService.deleteArticle(article);
        knowledgeBaseService.getLatestArticle(article.getResourcePrimKey());
    }

    @Test(alwaysRun = true, groups = {"deleteData"}, dependsOnGroups = {"articleAccess",
            "siteAccess"}, dependsOnMethods = {"articleDelete"})
    public void deleteRepository() throws ClientProtocolException, NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, RepositoryNotDeletedException, WebServiceAccessError {
        Assert.assertTrue(repositoryService.deleteRepository(repository));
    }

    @Test(alwaysRun = true, groups = {"deleteData"}, dependsOnGroups = {"articleAccess",
            "siteAccess"}, dependsOnMethods = {"deleteRepository"})
    public void deleteSite() throws ClientProtocolException, NotConnectedToWebServiceException, IOException,
            AuthenticationRequired, WebServiceAccessError {
        IGroup<Long> site = siteService.getSite(company, SITE_NAME);
        Assert.assertTrue(siteService.deleteSite(site));
    }

    @AfterClass
    public void closeConnections() {
        knowledgeBaseService.disconnect();
        companyService.disconnect();
        siteService.disconnect();
    }
}
