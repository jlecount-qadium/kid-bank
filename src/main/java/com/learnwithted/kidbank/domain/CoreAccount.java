package com.learnwithted.kidbank.domain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class CoreAccount implements Account {
  private final TransactionRepository transactionRepository;
  private final GoalRepository goalRepository;
  private BalanceChangedNotifier balanceChangedNotifier = (amount, balance) -> {
  };
  private ImmutableSet<Transaction> transactions;
  private ImmutableSet<Goal> goals;

  @Autowired
  public CoreAccount(TransactionRepository transactionRepository,
      GoalRepository goalRepository, BalanceChangedNotifier balanceChangedNotifier) {
    this.transactionRepository = transactionRepository;
    this.transactions = ImmutableSet.<Transaction>builder()
                            .addAll(transactionRepository.findAll())
                            .build();
    this.goalRepository = goalRepository;
    this.goals = ImmutableSet.<Goal>builder()
                     .addAll(goalRepository.findAll())
                     .build();
    this.balanceChangedNotifier = balanceChangedNotifier;
  }

  @Override
  public int balance() {
    return balanceUpTo(LocalDateTime.MAX);
  }

  @Override
  public void deposit(LocalDateTime transactionDateTime, int amount, String source) {
    Transaction deposit = Transaction.createDeposit(transactionDateTime, amount, source);
    addNewTransaction(deposit);
    balanceChangedNotifier.balanceChanged(amount, transactions.stream()
                                                              .mapToInt(Transaction::signedAmount)
                                                              .sum());
  }

  @Override
  public void spend(LocalDateTime transactionDateTime, int amount, String description) {
    Transaction spend = Transaction.createSpend(transactionDateTime, amount, description);
    addNewTransaction(spend);
    balanceChangedNotifier.balanceChanged(-amount, transactions.stream()
                                                               .mapToInt(Transaction::signedAmount)
                                                               .sum());
  }

  private void addNewTransaction(Transaction transaction) {
    Transaction savedTransaction = transactionRepository.save(transaction);
    transactions = ImmutableSet.<Transaction>builder()
                       .addAll(transactions)
                       .add(savedTransaction)
                       .build();
  }

  @Override
  public ImmutableList<Transaction> transactions() {
    return ImmutableList.copyOf(transactions);
  }

  @Override
  public void load(List<Transaction> transactionsToLoad) {
    List<Transaction> savedTransactions = transactionRepository.saveAll(transactionsToLoad);
    transactions = ImmutableSet.<Transaction>builder()
                       .addAll(transactions)
                       .addAll(savedTransactions)
                       .build();
  }

  /**
   * Calculates Balance just prior to (exclusive) the given date
   */
  @Override
  public int balanceUpTo(LocalDateTime localDateTime) {
    return transactions
               .stream()
               .filter(t -> t.dateTime().isBefore(localDateTime))
               .mapToInt(Transaction::signedAmount)
               .sum();
  }

  @Override
  public Set<Goal> goals() {
    return ImmutableSet.copyOf(goals);
  }

  @Override
  public void createGoal(String description, int targetAmount) {
    Goal goal = new Goal(description, targetAmount);
    Goal savedGoal = goalRepository.save(goal);
    goals = ImmutableSet.<Goal>builder()
                .addAll(goals)
                .add(savedGoal)
                .build();
  }
}