class BankAccount(amount: Int) {
    init {
        require(amount >= 0)
    }
    var balance: Int = amount
        private set(newBalance) {
            logTransaction(balance, newBalance)
            field = newBalance
        }
    fun deposit(amount: Int) {
        require(amount > 0)
        balance += amount
    }
    fun withdraw(amount: Int) {
        require(amount in 1..balance)
        balance -= amount
    }
}

fun logTransaction(from: Int, to: Int) {
    println("$from -> $to")
}
