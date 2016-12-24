package entities;

public class Player {
    private String name = "";
    private Room room;
    private Card first_card;
    private Card second_card;
    private int money;
    private int bet = 0;
    private boolean turned = false;
    private Combination best_comb;

    public Player() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Card getFirst_card() {
        return first_card;
    }

    public void setFirst_card(Card first_card) {
        this.first_card = first_card;
    }

    public Card getSecond_card() {
        return second_card;
    }

    public void setSecond_card(Card second_card) {
        this.second_card = second_card;
    }

    public void bet(int amount) throws Exception {
        if (money >= amount) {
            bet = amount;
            money -= amount;
        }
        else {
            throw new Exception("You doesn't have enough money to start the game. You should to exit from the game. " +
                    "Use exit from game command");
        }
    }

    public void raise(int amount) throws Exception {
        if (this.money > amount + room.getGame().getMax() - bet) {
            bet = room.getGame().getMax() + amount;
            money -= Math.abs(room.getGame().getMax() - amount);
        }
        else {
            throw new Exception("You doesn't have enough money. You should bet all your money. Use all-in command to do it. " +
                    "Also you can fold the cards by using fold command.");
        }
    }

    public void call() throws Exception {
        if (money > room.getGame().getMax()) {
            if (room.getGame().getMax() > 0) {
                bet = room.getGame().getMax();
            }
            else {
                throw new Exception("You cannot to call, because game is started already.");
            }
        }
        else {
            throw new Exception("You doesn't have enough money. You should bet all your money. Use all-in command to do it. " +
                    "Also you can fold the cards by using fold command.");
        }
    }

    public void all_in() {
        bet += money;
        money = 0;
    }

    public void fold() {
        /*
        Сбросить карты
         */
    }

    public void check() {
        /*
        Оставить "как есть"
         */
    }

    public int getBet() {
        return bet;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public boolean isTurned() {
        boolean flag = turned;
        if (turned) {
            turned = false;
        }
        return flag;
    }

    public void setTurned() {
        turned = true;
    }

    public void setBest_comb(Combination combination) {
        this.best_comb = combination;
    }

    public Combination getBest_comb() {
        return best_comb;
    }
}
