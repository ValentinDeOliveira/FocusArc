---
title: "ADR-001: Overtime"
sidebar_position: 1
---

# ADR-001: Overtime

## ステータス
承認済み

## 文脈

ユーザーがタスクに取り組むとき、推定時間が終わると、止めるとは限りません。
セッションを中断したり、入力を求めたりすることは、集中を切ってしまいます。
それは集中はこのアプリの核心です。

## 決定

タスクのタイマーは`endAt`に達すると、「**オーバータイムモード**」に自動的に入る。
タイマーが続け作動しています。

ユーザーがやがてタスクを止めると、`completedMinutes`は次のようには記録させます：
```
completedMinutes = estimatedMinutes + overtime duration
```

## 結果

- 推定時間を終わっても、ユーザーは集中中に絶対に中断されません
- `completedMinutes`は`estimatedMinutes`を超えることがあります。これは仕様通りです.
- 再計算のカスケード（チャプター → アーク）は、`completedMinutes > estimatedMinutes`
  をエラーとして扱うことなく処理する必要があります。