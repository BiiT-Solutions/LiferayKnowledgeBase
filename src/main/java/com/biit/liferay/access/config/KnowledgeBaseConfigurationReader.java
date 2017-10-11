package com.biit.liferay.access.config;

import com.biit.usermanager.entity.pool.config.PoolConfigurationReader;
import com.biit.utils.configuration.ConfigurationReader;
import com.biit.utils.configuration.PropertiesSourceFile;
import com.biit.utils.configuration.SystemVariablePropertiesSourceFile;
import com.biit.utils.configuration.exceptions.PropertyNotFoundException;

public class KnowledgeBaseConfigurationReader extends ConfigurationReader {
	private static final String CONFIG_FILE = "settings.conf";
	private static final String SYSTEM_VARIABLE_CONFIG = "USER_MANAGER_CONFIG";

	// Tags
	private static final String ARTICLE_EXPIRATION_TIME = "knowledgebase.article.pool.expiration";
	private static final String FILE_EXPIRATION_TIME = "knowledgebase.file.pool.expiration";

	// Default
	private static final String DEFAULT_EXPIRATION_TIME = "300000";

	private static KnowledgeBaseConfigurationReader instance;

	private KnowledgeBaseConfigurationReader() {
		super();

		addProperty(ARTICLE_EXPIRATION_TIME, DEFAULT_EXPIRATION_TIME);
		addProperty(FILE_EXPIRATION_TIME, DEFAULT_EXPIRATION_TIME);

		addPropertiesSource(new PropertiesSourceFile(CONFIG_FILE));
		addPropertiesSource(new SystemVariablePropertiesSourceFile(SYSTEM_VARIABLE_CONFIG, CONFIG_FILE));

		readConfigurations();
	}

	public static KnowledgeBaseConfigurationReader getInstance() {
		if (instance == null) {
			synchronized (PoolConfigurationReader.class) {
				if (instance == null) {
					instance = new KnowledgeBaseConfigurationReader();
				}
			}
		}
		return instance;
	}

	private String getPropertyLogException(String propertyId) {
		try {
			return getProperty(propertyId);
		} catch (PropertyNotFoundException e) {
			return null;
		}
	}

	public Long getArticlePoolExpirationTime() {
		try {
			return Long.parseLong(getPropertyLogException(ARTICLE_EXPIRATION_TIME));
		} catch (Exception e) {
			return Long.parseLong(DEFAULT_EXPIRATION_TIME);
		}
	}

	public Long getFileEntryPoolExpirationTime() {
		try {
			return Long.parseLong(getPropertyLogException(FILE_EXPIRATION_TIME));
		} catch (Exception e) {
			return Long.parseLong(DEFAULT_EXPIRATION_TIME);
		}
	}

}
