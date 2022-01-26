package at.htl.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Penalty {

    @Id
    private Long paymentNo;
    private LocalDate penDate;
    private BigDecimal amount;
    @ManyToOne
    private Player player;

    public Long getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(Long paymentNo) {
        this.paymentNo = paymentNo;
    }

    public LocalDate getPenDate() {
        return penDate;
    }

    public void setPenDate(LocalDate penDate) {
        this.penDate = penDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
