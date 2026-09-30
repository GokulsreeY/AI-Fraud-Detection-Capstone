import random
import csv

ROWS = 10000

with open("payments_training.csv", "w", newline="") as file:
    writer = csv.writer(file)

    writer.writerow([
        "amount",
        "hour_of_day",
        "day_of_week",
        "transaction_count_1h",
        "average_amount_30d",
        "is_new_merchant",
        "is_fraud"
    ])

    for _ in range(ROWS):

        amount = round(random.uniform(5, 5000), 2)
        hour = random.randint(0, 23)
        day = random.randint(0, 6)

        transaction_count = random.randint(0, 15)

        average_amount = round(
            random.uniform(20, 1000),
            2
        )

        is_new_merchant = random.randint(0, 1)

        # Synthetic fraud rules
        risk = 0

        if amount > 3000:
            risk += 2

        if hour <= 4:
            risk += 1

        if transaction_count > 10:
            risk += 2

        if amount > average_amount * 5:
            risk += 2

        if is_new_merchant:
            risk += 1

        is_fraud = 1 if risk >= 4 else 0

        writer.writerow([
            amount,
            hour,
            day,
            transaction_count,
            average_amount,
            is_new_merchant,
            is_fraud
        ])