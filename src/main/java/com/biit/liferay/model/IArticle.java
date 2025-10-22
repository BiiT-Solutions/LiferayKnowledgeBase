package com.biit.liferay.model;

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

import java.util.Calendar;
import java.util.List;

import com.biit.usermanager.entity.IElement;
import com.biit.utils.pool.PoolElement;

public interface IArticle<ArticleId> extends IElement<ArticleId>, PoolElement<ArticleId> {

	Long getResourcePrimKey();

	String getTitle();

	String getContent();

	String getDescription();

	List<String> getSections();

	void setSections(List<String> sections);

	int getViewCount();

	int getVersion();

	void setTitle(String title);

	void setDescription(String description);

	void setContent(String content);

	Calendar getCreateDate();
}
