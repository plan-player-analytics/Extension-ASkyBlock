/*
    Copyright(c) 2019 Risto Lahtela (Rsl1122)

    The MIT License(MIT)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files(the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions :
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/
package com.djrapitops.extension;

import com.djrapitops.plan.extension.CallEvents;
import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.ElementOrder;
import com.djrapitops.plan.extension.annotation.*;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.table.Table;
import com.wasteofplastic.askyblock.ASkyBlockAPI;

import java.util.Map;
import java.util.UUID;

/**
 * Template for new DataExtension.
 *
 * @author Rsl1122
 */
@PluginInfo(name = "ASkyBlock", iconName = "street-view", iconFamily = Family.SOLID, color = Color.LIGHT_BLUE)
@TabInfo(
        tab = "Islands",
        iconName = "street-view",
        elementOrder = {ElementOrder.VALUES}
)
@TabInfo(
        tab = "Challenges",
        iconName = "bookmark",
        elementOrder = {ElementOrder.VALUES}
)
@TabOrder({"Islands", "Challenges"})
public class ASkyBlockExtension implements DataExtension {

    public ASkyBlockExtension() {
    }

    @Override
    public CallEvents[] callExtensionMethodsOn() {
        return new CallEvents[]{
                CallEvents.PLAYER_JOIN,
                CallEvents.PLAYER_LEAVE,
                CallEvents.SERVER_PERIODICAL
        };
    }

    @BooleanProvider(
            text = "Has Island",
            description = "Does the player have a skyblock island",
            conditionName = "hasIsland",
            iconName = "street-view",
            iconColor = Color.GREEN,
            priority = 100
    )
    @Tab("Islands")
    public boolean hasIsland(UUID playerUUID) {
        return ASkyBlockAPI.getInstance().hasIsland(playerUUID);
    }

    @Conditional("hasIsland")
    @StringProvider(
            text = "Island name",
            description = "Name of the player's island",
            iconName = "street-view",
            iconColor = Color.GREEN,
            priority = 99,
            showInPlayerTable = true
    )
    @Tab("Islands")
    public String islandName(UUID playerUUID) {
        String name = ASkyBlockAPI.getInstance().getIslandName(playerUUID);
        return name != null ? name : "-";
    }

    @Conditional("hasIsland")
    @NumberProvider(
            text = "Island level",
            description = "Level of the player's island",
            iconName = "street-view",
            iconColor = Color.AMBER,
            priority = 98
    )
    @Tab("Islands")
    public long islandLevel(UUID playerUUID) {
        return ASkyBlockAPI.getInstance().getLongIslandLevel(playerUUID);
    }

    @Conditional("hasIsland")
    @NumberProvider(
            text = "Island Resets Left",
            description = "How many times can the player reset their island",
            iconName = "street-view",
            iconColor = Color.GREEN,
            priority = 97
    )
    @Tab("Islands")
    public long islandResets(UUID playerUUID) {
        return ASkyBlockAPI.getInstance().getResetsLeft(playerUUID);
    }

    @NumberProvider(
            text = "Completed Challenges",
            description = "How many challenges has the player completed",
            iconName = "bookmark",
            iconColor = Color.GREEN,
            priority = 100,
            showInPlayerTable = true
    )
    @Tab("Challenges")
    public long challengesCompleted(UUID playerUUID) {
        return ASkyBlockAPI.getInstance().getChallengeStatus(playerUUID).values().stream().filter(value -> value).count();
    }

    @PercentageProvider(
            text = "Progress",
            description = "% out of total challenges completed",
            iconName = "bookmark",
            iconColor = Color.GREEN,
            priority = 99
    )
    @Tab("Challenges")
    public double challengeProgress(UUID playerUUID) {
        Map<String, Boolean> challengeStatus = ASkyBlockAPI.getInstance().getChallengeStatus(playerUUID);
        double max = challengeStatus.size();
        return challengeStatus.values().stream().filter(value -> value).count() * 1.0 / max;
    }

    @TableProvider(tableColor = Color.LIGHT_BLUE)
    @Tab("Challenges")
    public Table completedChallenges(UUID playerUUID) {
        Map<String, Integer> challengeTimes = ASkyBlockAPI.getInstance().getChallengeTimes(playerUUID);
        Table.Factory table = Table.builder()
                .columnOne("Challenge", Icon.called("bookmark").build())
                .columnTwo("Times completed", Icon.called("check").build());

        challengeTimes.entrySet().stream()
                .sorted((one, two) -> Integer.compare(two.getValue(), one.getValue()))
                .forEach(entry -> {
                    String challenge = entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1).toLowerCase();
                    Integer completionTimes = entry.getValue();
                    boolean complete = completionTimes > 0;
                    table.addRow(
                            "<span" + (complete ? " class=\"col-green\"" : "") + ">" + challenge + "</span>",
                            completionTimes
                    );
                });

        return table.build();
    }

    @StringProvider(
            text = "Island World",
            description = "What world is used for ASkyBlock islands",
            priority = 102,
            iconName = "map",
            iconColor = Color.GREEN,
            iconFamily = Family.REGULAR
    )
    @Tab("Islands")
    public String islandWorld() {
        return ASkyBlockAPI.getInstance().getIslandWorld().getName();
    }

    @NumberProvider(
            text = "Island Count",
            description = "How many islands exist",
            priority = 101,
            iconName = "street-view",
            iconColor = Color.GREEN
    )
    @Tab("Islands")
    public long islandCount() {
        return ASkyBlockAPI.getInstance().getIslandCount();
    }

}