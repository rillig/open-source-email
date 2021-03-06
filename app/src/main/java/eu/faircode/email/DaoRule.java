package eu.faircode.email;

/*
    This file is part of FairEmail.

    FairEmail is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FairEmail is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FairEmail.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2018-2019 by Marcel Bokhorst (M66B)
*/

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DaoRule {
    @Query("SELECT * FROM rule" +
            " WHERE folder = :folder" +
            " ORDER BY `order`")
    List<EntityRule> getRules(long folder);

    @Query("SELECT * FROM rule" +
            " WHERE folder = :folder" +
            " AND enabled" +
            " ORDER BY `order`")
    List<EntityRule> getEnabledRules(long folder);

    @Query("SELECT rule.*, folder.account, folder.name AS folderName, account.name AS accountName FROM rule" +
            " JOIN folder ON folder.id = rule.folder" +
            " JOIN account ON account.id = folder.account" +
            " WHERE rule.id = :id")
    TupleRuleEx getRule(long id);

    @Query("SELECT rule.*, folder.account, folder.name AS folderName, account.name AS accountName FROM rule" +
            " JOIN folder ON folder.id = rule.folder" +
            " JOIN account ON account.id = folder.account" +
            " WHERE rule.folder = :folder")
    LiveData<List<TupleRuleEx>> liveRules(long folder);

    @Insert
    long insertRule(EntityRule rule);

    @Update
    int updateRule(EntityRule rule);

    @Query("DELETE FROM rule WHERE id = :id")
    void deleteRule(long id);
}
