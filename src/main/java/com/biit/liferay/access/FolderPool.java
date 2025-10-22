package com.biit.liferay.access;

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

import com.biit.liferay.access.config.KnowledgeBaseConfigurationReader;
import com.biit.liferay.model.IFolder;
import com.biit.utils.pool.SimplePool;

public class FolderPool extends SimplePool<Long, IFolder<Long>> {

	private static FolderPool instance = new FolderPool();

	public static FolderPool getInstance() {
		return instance;
	}

	@Override
	public boolean isDirty(IFolder<Long> element) {
		return false;
	}

	@Override
	public long getExpirationTime() {
		return KnowledgeBaseConfigurationReader.getInstance().getFolderPoolExpirationTime();
	}

}
