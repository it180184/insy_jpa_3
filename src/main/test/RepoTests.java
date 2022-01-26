import at.htl.Repository;
import at.htl.model.Penalty;
import at.htl.model.Player;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class RepoTests {

    private final Repository repo;

    public RepoTests(Repository repo) {
        this.repo = repo;
    }

    @Test
    public void test01() {
        var results = this.repo.getPlayersLivingInTown("Eltham");
        assertThat(results).isNotEmpty().hasSize(2);
        assertThat(getPlayerNames(results)).containsAll(List.of("Collins", "Moorman"));
    }

    @Test
    public void test02() {
        var results = this.repo.getPlayersLivingInTown("Washington");
        assertThat(results).isEmpty();
    }

    @Test
    public void test03() {
        var results = this.repo.getPlayersLivingInTowns(List.of("Eltham", "Plymouth"));
        assertThat(results).isNotEmpty().hasSize(3);
        assertThat(getPlayerNames(results)).containsAll(List.of("Collins", "Moorman", "Bailey"));
    }

    @Test
    public void test04() {
        var results = this.repo.getPlayersLivingInTowns(List.of());
        assertThat(results).isEmpty();
    }

    @Test
    public void test05() {
        var results = this.repo.getPlayersLivingInTowns(List.of("London", "Paris", "Tokyo"));
        assertThat(results).isEmpty();
    }

    @Test
    public void test06() {
        var results = this.repo.getPlayersWithGenderAndAge(false, 1960);
        assertThat(results).isNotEmpty().hasSize(3);
        assertThat(getPlayerNames(results)).containsAll(List.of("Bishop", "Everett", "Hope"));
    }

    @Test
    public void test07() {
        var results = this.repo.getPlayersWithGenderAndAge(true, 1970);
        assertThat(results).isNotEmpty().hasSize(4);
        assertThat(getPlayerNos(results)).containsAll(List.of(27L, 112L, 8L, 28L));
    }

    @Test
    public void test08() {
        var results = this.repo.getPenaltiesInDateRange(LocalDate.of(1981, 6, 1),
                LocalDate.of(1983, 9, 10));
        assertThat(results).isNotEmpty().hasSize(2);
        assertThat(getPenaltyNos(results)).containsAll(List.of(3L, 7L));
    }

    @Test
    public void test09() {
        var results = this.repo.getPenaltiesWithAmountHigherEqualThan(BigDecimal.valueOf(50));
        assertThat(results).isNotEmpty().hasSize(5);
        assertThat(getPenaltyNos(results)).containsAll(List.of(1L, 2L, 3L, 4L, 8L));
    }

    @Test
    public void test10() {
        var result = this.repo.getAveragePenaltyAmount();
        assertThat(result).isEqualTo(60D);
    }

    @Test
    public void test11() {
        var result = this.repo.getMinMaxPenaltyAmount();
        assertThat(result.minAmount().compareTo(BigDecimal.valueOf(25))).isEqualTo(0);
        assertThat(result.maxAmount().compareTo(BigDecimal.valueOf(100))).isEqualTo(0);
    }

    @Test
    public void test12() {
        var results = this.repo.getPlayersWithPenalties(true);
        assertThat(results).isNotEmpty().hasSize(5);
        assertThat(getPlayerNos(results)).containsAll(List.of(6L, 44L, 27L, 104L, 8L));
    }

    @Test
    public void test13() {
        var results = this.repo.getPlayersWithPenalties(false);
        assertThat(results).isNotEmpty().hasSize(9);
        assertThat(getPlayerNos(results)).containsAll(List.of(83L, 2L, 7L, 57L, 39L, 112L, 100L, 28L, 95L));
    }

    @Test
    public void test14() {
        var results = this.repo.getTownsWithPlayerNumber(7L);
        assertThat(results).isNotEmpty().hasSize(1);
        assertThat(results).contains("Stratford");
    }

    @Test
    public void test15() {
        var results = this.repo.getTownsWithPlayerNumber(2L);
        assertThat(results).isNotEmpty().hasSize(3);
        assertThat(results).containsAll(List.of("Stratford", "Inglewood", "Eltham"));
    }

    @Test
    public void test16() {
        var results = this.repo.getTownsWithPlayerNumber(10L);
        assertThat(results).isEmpty();
    }

    @Test
    public void test17() {
        final char male = 'M';
        final char female = 'F';

        var result = this.repo.getPlayerCountsByGender();
        assertThat(result).isNotNull().isNotEmpty().hasSize(2);
        assertThat(result.keySet()).containsAll(List.of(female, male));
        assertThat(result.get(female)).isEqualTo(5);
        assertThat(result.get(male)).isEqualTo(9);
    }

    @Test
    public void test18() {
        var sums = new HashMap<Long, BigDecimal>() {
            {
                put(104L, BigDecimal.valueOf(50));
                put(6L, BigDecimal.valueOf(100));
                put(27L, BigDecimal.valueOf(175));
                put(8L, BigDecimal.valueOf(25));
                put(44L, BigDecimal.valueOf(130));
            }
        };

        var results = this.repo.getPenaltiesForAllPlayers();
        assertThat(results).isNotEmpty().hasSize(14);
        for (var pp : results) {
            if (!sums.containsKey(pp.player().getPlayerNo())) {
                assertThat(pp.penaltySum().compareTo(BigDecimal.valueOf(0))).isEqualTo(0);
                continue;
            }
            assertThat(pp.penaltySum().compareTo(sums.get(pp.player().getPlayerNo()))).isEqualTo(0);
        }
    }

    private static List<Long> getPlayerNos(List<Player> players) {
        return players.stream()
                .map(Player::getPlayerNo)
                .toList();
    }

    private static List<Long> getPenaltyNos(List<Penalty> penalties) {
        return penalties.stream()
                .map(Penalty::getPaymentNo)
                .toList();
    }

    private static List<String> getPlayerNames(List<Player> players) {
        return players.stream()
                .map(Player::getName)
                .toList();
    }
}
