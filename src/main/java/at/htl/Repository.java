package at.htl;

import at.htl.model.Penalty;
import at.htl.model.Player;
import at.htl.results.GenderCount;
import at.htl.results.MinMaxAmount;
import at.htl.results.PlayerPenalties;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class Repository {

    private final EntityManager entityManager;

    public Repository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Returns players living in a specified town
     *
     * @param town name of the town
     */
    public List<Player> getPlayersLivingInTown(String town) {
        var q = entityManager.createQuery("select p from Player p where p.town = :town", Player.class);
        q.setParameter("town", town);
        return q.getResultList();
    }

    /**
     * Returns players living in one of the specified towns
     *
     * @param towns names of towns
     */
    public List<Player> getPlayersLivingInTowns(List<String> towns) {
        var q = entityManager.createQuery("select p from Player p where p.town in :towns", Player.class);
        q.setParameter("towns", towns);
        return q.getResultList();
    }

    /**
     * Returns players of a certain gender born before a specified year
     *
     * @param female         male or female
     * @param bornBeforeYear the exclusive year before someone has to be born
     */
    public List<Player> getPlayersWithGenderAndAge(boolean female, int bornBeforeYear) {
        var q = entityManager.createQuery("select p from Player p where p.sex = :sex and p.yearOfBirth < :year", Player.class);
        q.setParameter("sex", female ? 'F' : 'M');
        q.setParameter("year", bornBeforeYear);
        return q.getResultList();
    }

    /**
     * Returns penalties issued between two dates
     *
     * @param start the first (earlier) date, inclusive
     * @param end   the second (later) date, inclusive
     */
    public List<Penalty> getPenaltiesInDateRange(LocalDate start, LocalDate end) {
        var q = entityManager.createQuery("select p from Penalty p where p.penDate between :start and :end", Penalty.class);
        q.setParameter("start", start);
        q.setParameter("end", end);
        return q.getResultList();
    }

    /**
     * Returns penalties with an amount higher or equal to the specified amount
     */
    public List<Penalty> getPenaltiesWithAmountHigherEqualThan(BigDecimal amount) {
        var q = entityManager.createQuery("select p from Penalty p where p.amount >= :amount", Penalty.class);
        q.setParameter("amount", amount);
        return q.getResultList();
    }

    /**
     * Returns the average penalty sum calculated over all penalties
     */
    public Double getAveragePenaltyAmount() {
        return this.entityManager.createQuery("select avg(p.amount) from Penalty p", Double.class).getSingleResult();
    }

    /**
     * Returns the min & max penalty amount
     */
    public MinMaxAmount getMinMaxPenaltyAmount() {
        return entityManager.createQuery("select new at.htl.results.MinMaxAmount(min(p.amount), max(p.amount)) from Penalty p", MinMaxAmount.class).getSingleResult();
    }

    /**
     * Returns all players who either have or have not received a penalty so far
     *
     * @param hasPenalty flag indicating if we want to look for players with or without penalties
     */
    public List<Player> getPlayersWithPenalties(boolean hasPenalty) {
        TypedQuery<Player> q;
        if (hasPenalty)
            q = entityManager.createQuery("select p from Player p left join p.penalties pe group by p having count(pe) >= 1", Player.class);
        else
            q = entityManager.createQuery("select p from Player p left join p.penalties pe group by p having count(pe) = 0", Player.class);
        return q.getResultList();
    }

    /**
     * Returns the names of those towns who have at least as many players as specified
     *
     * @param minNoOfPlayers the min. number of players a town has to have
     */
    public List<String> getTownsWithPlayerNumber(Long minNoOfPlayers) {
        var q = entityManager.createQuery("select p.town from Player p group by p.town having count(p) >= :min", String.class);
        q.setParameter("min", minNoOfPlayers);
        return q.getResultList();
    }

    /**
     * Returns the number of players for each gender
     */
    public Map<Character, Long> getPlayerCountsByGender() {
        var q = entityManager.createQuery("select new at.htl.results.GenderCount(p.sex, count(p)) from Player p group by p.sex", GenderCount.class);
        return q.getResultStream().collect(Collectors.toMap(GenderCount::gender, GenderCount::count));
    }

    /**
     * Returns the penalty sum for all players, including those who never received a penalty (sum = 0)
     */
    public List<PlayerPenalties> getPenaltiesForAllPlayers() {
        var q = entityManager.createQuery("select new at.htl.results.PlayerPenalties(p, sum(coalesce(pe.amount, 0.0))) from Player p left join p.penalties pe group by p", PlayerPenalties.class);
        return q.getResultList();
    }
}
