/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.openapi.vcs.changes

import com.intellij.openapi.vcs.VcsBundle
import com.intellij.openapi.vcs.changes.shelf.ShelvedChangeList

object ChangeListUtil {

  private val CHANGELIST_NAME_PATTERN = "\\s\\[([\\s\\S]*)\\]"
  private val STASH_MESSAGE_PATTERN = VcsBundle.message("stash.changes.message", "[\\s\\S]*")
  private val SYSTEM_CHANGELIST_REGEX = (STASH_MESSAGE_PATTERN + CHANGELIST_NAME_PATTERN).toRegex()

  @JvmStatic fun createSystemShelvedChangeListName(systemPrefix: String, changelistName: String): String {
    return "$systemPrefix [$changelistName]"
  }

  fun getOriginalName(shelvedName: String): String {
    return SYSTEM_CHANGELIST_REGEX.matchEntire(shelvedName)?.groups?.get(1)?.value ?: shelvedName
  }

  @JvmStatic fun getPredefinedChangeList(shelvedList: ShelvedChangeList, changeListManager: ChangeListManager): LocalChangeList? {
    val defaultName = shelvedList.DESCRIPTION
    return changeListManager.findChangeList(defaultName) ?:
           if (shelvedList.isMarkedToDelete) changeListManager.findChangeList(getOriginalName(defaultName)) else null
  }

  @JvmStatic fun getChangeListNameForUnshelve(shelvedList: ShelvedChangeList): String {
    val defaultName = shelvedList.DESCRIPTION
    return if (shelvedList.isMarkedToDelete) getOriginalName(defaultName) else defaultName
  }
}
