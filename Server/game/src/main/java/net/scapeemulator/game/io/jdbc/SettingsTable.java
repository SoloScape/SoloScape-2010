package net.scapeemulator.game.io.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.scapeemulator.game.model.player.Player;

public final class SettingsTable extends Table {

	private final PreparedStatement loadStatement;
	private final PreparedStatement saveStatement;

	public SettingsTable(Connection connection) throws SQLException {
		this.loadStatement = connection.prepareStatement("SELECT * FROM settings WHERE player_id = ?;");
		this.saveStatement = connection.prepareStatement("INSERT INTO settings (player_id, setting, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE value = VALUES(value);");
	}

	@Override
	public void load(Player player) throws SQLException, IOException {
		loadStatement.setInt(1, player.getDatabaseId());

	}

	@Override
	public void save(Player player) throws SQLException, IOException {
		saveStatement.setInt(1, player.getDatabaseId());
	}
}
