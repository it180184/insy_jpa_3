package at.htl.results;

import at.htl.model.Player;

import java.math.BigDecimal;

public record PlayerPenalties(Player player, BigDecimal penaltySum) {}
