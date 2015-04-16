package com.biit.liferay.access;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.biit.liferay.access.exceptions.AuthenticationRequired;
import com.biit.liferay.access.exceptions.NotConnectedToWebServiceException;
import com.biit.liferay.access.exceptions.WebServiceAccessError;
import com.biit.liferay.model.KbArticle;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KnowledgeBaseService extends ServiceAccess<KbArticle> {

	public KnowledgeBaseService() {
	}

	public void reset() {
		ArticlePool.getInstance().reset();
	}

	@Override
	public List<KbArticle> decodeListFromJson(String json, Class<KbArticle> objectClass) throws JsonParseException,
			JsonMappingException, IOException {
		List<KbArticle> myObjects = new ObjectMapper().readValue(json, new TypeReference<List<KbArticle>>() {
		});
		return myObjects;
	}

	/**
	 * Gets latest published articled.
	 * 
	 * @param resourceKey
	 * @return
	 * @throws NotConnectedToWebServiceException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws AuthenticationRequired
	 * @throws WebServiceAccessError
	 */
	public KbArticle getLatestArticle(long resourceKey) throws NotConnectedToWebServiceException,
			ClientProtocolException, IOException, AuthenticationRequired, WebServiceAccessError {
		return getLatestArticle(resourceKey, 0);
	}

	/**
	 * Gets an article by its resource key and the status.
	 * 
	 * @param resourceKey
	 * @param status
	 *            0 if published.
	 * @return
	 * @throws NotConnectedToWebServiceException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws AuthenticationRequired
	 * @throws WebServiceAccessError
	 */
	public KbArticle getLatestArticle(long resourceKey, int status) throws NotConnectedToWebServiceException,
			ClientProtocolException, IOException, AuthenticationRequired, WebServiceAccessError {

		KbArticle article = ArticlePool.getInstance().getArticleByResourceKey(resourceKey);
		if (article != null) {
			return article;
		}
		checkConnection();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("resourcePrimKey", Long.toString(resourceKey)));
		params.add(new BasicNameValuePair("status", Integer.toString(status)));

		String result = getHttpResponse("knowledge-base-portlet.kbarticle/get-latest-kb-article", params);

		if (result != null) {
			// A Simple JSON Response Read
			article = decodeFromJson(result, KbArticle.class);
			ArticlePool.getInstance().addArticle(article);
			return article;
		}
		return null;
	}

}
