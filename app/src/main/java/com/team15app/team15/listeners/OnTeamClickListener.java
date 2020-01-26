package com.team15app.team15.listeners;

public interface OnTeamClickListener {
    void onTeamClick(String teamName);

    void onTeamLongClick(boolean isSelected);

    void onJerseyClick(boolean isGoalkeeper, int drawable);
}
