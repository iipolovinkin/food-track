 food-track

**Название**: FoodTrack — аналитика для фуд-стартапа

Делаем **Event Tracker для приложения доставки еды** с двумя категориями: **пицца** и **бургеры**.  
Цель — понять поведение пользователей и измерить эффективность воронки:  
**открытие → просмотр → добавление в корзину → заказ**.

План для MVP:
- **Структура события** — универсальная, с `properties` как JSON.
- **Набор ключевых событий** — ровно столько, сколько нужно для аналитики.
- **Как хранить** — PostgreSQL + JSONB.
- **Какие метрики считать** — DAU, воронка, популярность.
- **Демо-данные**: скрипт генерит N пользователей с цепочками событий.

---

## 🧱 1. Структура события (Event)

Будем использовать **единый гибкий формат**, подходящий для всех типов событий.

### JSON-структура (то, что приложение отправляет на `/track`):
```json
{
  "eventType": "screen_viewed",
  "userId": "user_789",
  "sessionId": "sess_abc123",
  "timestamp": "2024-05-20T18:42:10Z",
  "properties": {
    "screen": "menu",
    "category": "pizza"
  }
}
```

### Обязательные поля:
| Поле | Тип | Описание |
|------|-----|--------|
| `eventType` | string | Название события (`screen_viewed`, `item_added`, и т.д.) |
| `userId` | string | Уникальный ID пользователя (если не авторизован — можно генерировать временный) |
| `sessionId` | string | ID сессии (полезен для анализа пути пользователя) |
| `timestamp` | ISO 8601 | Время события (лучше генерировать на клиенте) |

### Опциональные поля (в `properties` — `Map<String, Object>`):
- Любые контекстные данные: `item_id`, `price`, `category`, `screen`, `error_code` и т.д.

> 💡 В Java будем хранить `properties` как `Map<String, Object>`, а в PostgreSQL — как `JSONB`.

---

## 📋 2. Какие события отслеживать?

Для аналитики воронки и поведения достаточно **5–6 ключевых событий**:

| Событие (`eventType`) | Когда отправляется | Пример `properties` |
|------------------------|-------------------|----------------------|
| `app_opened` | При запуске приложения | `{ "platform": "android", "version": "1.2.0" }` |
| `screen_viewed` | Пользователь открыл экран | `{ "screen": "menu", "category": "pizza" }` |
| `item_viewed` | Открыл карточку товара | `{ "item_id": "pizza_pepperoni", "category": "pizza", "price": 599 }` |
| `item_added_to_cart` | Добавил в корзину | `{ "item_id": "burger_classic", "category": "burger", "quantity": 2 }` |
| `checkout_started` | Перешёл к оплате | `{ "cart_total": 1198, "items_count": 2 }` |
| `order_placed` | Успешно заказал | `{ "order_id": "ord_12345", "total": 1198, "payment_method": "card" }` |
| `payment_failed` | Оплата не прошла | `{ "error_code": "card_declined", "order_id": "ord_12345" }` |

> ✅ Этих событий достаточно, чтобы построить:
> - Воронку конверсии
> - Популярность пицц vs бургеров
> - Средний чек
> - Проблемы с оплатой

---

## 🗃 3. Как хранить в базе (PostgreSQL)

Таблица `events`:

```sql
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    session_id VARCHAR(100) NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    properties JSONB
);

-- Индексы для аналитики
CREATE INDEX idx_events_type_time ON events (event_type, timestamp);
CREATE INDEX idx_events_user ON events (user_id);
CREATE INDEX idx_events_props_category ON events ((properties->>'category'));
```

> 💡 `JSONB` позволяет делать запросы вроде:
> ```sql
> SELECT COUNT(*) FROM events 
> WHERE event_type = 'item_viewed' 
>   AND (properties->>'category') = 'pizza';
> ```

---

## 📊 4. Какие метрики можно строить?

На основе этих событий:

1. **DAU**:
   ```sql
   SELECT COUNT(DISTINCT user_id) 
   FROM events 
   WHERE event_type = 'app_opened' 
     AND timestamp >= '2024-05-20';
   ```

2. **Воронка конверсии** (pizza):
    - Уникальные пользователи, просмотревшие пиццу → добавившие → заказавшие.

3. **ТОП-5 популярных блюд**:
   ```sql
   SELECT properties->>'item_id' AS item, COUNT(*) 
   FROM events 
   WHERE event_type = 'item_viewed' 
   GROUP BY item 
   ORDER BY COUNT DESC 
   LIMIT 5;
   ```

4. **Конверсия из корзины в заказ**:  
   `(число order_placed) / (число checkout_started) * 100%`

---

## 📱 5. Как приложение будет отправлять события?

Пример на Kotlin (Android) или Swift (iOS), но логика одинаковая:

```kotlin
fun track(eventType: String, properties: Map<String, Any>) {
    val event = mapOf(
        "eventType" to eventType,
        "userId" to getCurrentUserId(),
        "sessionId" to getCurrentSessionId(),
        "timestamp" to Instant.now().toString(),
        "properties" to properties
    )
    api.post("/track", event)
}
```

Использование:
```kotlin
track("item_viewed", mapOf(
    "item_id" to "pizza_margarita",
    "category" to "pizza",
    "price" to 499
))
```
