# Inventory Management System - Spring Boot Backend

A comprehensive Spring Boot REST API for inventory management with JWT authentication, forecasting capabilities, and feature-based architecture.

## 🚀 Features

- **User Authentication & Authorization** with JWT
- **Complete Inventory Management** (CRUD operations)
- **Advanced Search & Filtering**
- **Demand Forecasting** with insights
- **Real-time Metrics & Analytics**
- **Role-based Access Control**
- **RESTful API Design**
- **Comprehensive Exception Handling**
- **Data Validation**

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/inventorymanager/
│   │   ├── InventoryManagementApplication.java
│   │   ├── common/
│   │   │   ├── dto/
│   │   │   │   └── Response.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   └── InvalidOperationException.java
│   │   │   └── service/
│   │   │       └── DataSeederService.java
│   │   ├── features/
│   │   │   ├── auth/
│   │   │   │   ├── controller/
│   │   │   │   │   └── AuthController.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── LoginRequestDto.java
│   │   │   │   │   ├── LoginResponseDto.java
│   │   │   │   │   ├── RegisterRequestDto.java
│   │   │   │   │   └── UserDto.java
│   │   │   │   ├── entity/
│   │   │   │   │   ├── User.java
│   │   │   │   │   └── Role.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── UserRepository.java
│   │   │   │   └── service/
│   │   │   │       ├── AuthService.java
│   │   │   │       └── impl/
│   │   │   │           └── AuthServiceImpl.java
│   │   │   ├── inventory/
│   │   │   │   ├── controller/
│   │   │   │   │   └── InventoryController.java
│   │   │   │   ├── dao/
│   │   │   │   │   └── InventoryDao.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── InventoryItemDto.java
│   │   │   │   │   ├── CreateInventoryItemDto.java
│   │   │   │   │   ├── UpdateInventoryItemDto.java
│   │   │   │   │   ├── InventoryFilterDto.java
│   │   │   │   │   └── InventoryMetricsDto.java
│   │   │   │   ├── entity/
│   │   │   │   │   ├── InventoryItem.java
│   │   │   │   │   ├── Category.java
│   │   │   │   │   ├── Supplier.java
│   │   │   │   │   └── StockStatus.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── InventoryItemRepository.java
│   │   │   │   │   ├── CategoryRepository.java
│   │   │   │   │   └── SupplierRepository.java
│   │   │   │   └── service/
│   │   │   │       ├── InventoryService.java
│   │   │   │       └── impl/
│   │   │   │           └── InventoryServiceImpl.java
│   │   │   └── forecasting/
│   │   │       ├── controller/
│   │   │       │   └── ForecastController.java
│   │   │       ├── dto/
│   │   │       │   ├── ForecastDto.java
│   │   │       │   ├── ProductForecastDto.java
│   │   │       │   └── ForecastInsightDto.java
│   │   │       └── service/
│   │   │           ├── ForecastService.java
│   │   │           └── impl/
│   │   │               └── ForecastServiceImpl.java
│   │   └── security/
│   │       ├── JwtTokenProvider.java
│   │       ├── JwtAuthenticationFilter.java
│   │       └── SecurityConfig.java
│   └── resources/
│       ├── application.yml
│       └── data.sql
```

## 🛠️ Technologies Used

- **Spring Boot 3.2.0**
- **Spring Security 6** with JWT
- **Spring Data JPA**
- **H2 Database** (Development)
- **PostgreSQL** (Production)
- **Lombok** for reducing boilerplate
- **Gradle** for build management
- **Bean Validation** for input validation

## 📋 Prerequisites

- Java 17 or higher
- Gradle 7.0 or higher

## 🚀 Getting Started

### 1. Clone the repository
```bash
git clone <repository-url>
cd inventory-management-backend
```

### 2. Build the project
```bash
./gradlew build
```

### 3. Run the application
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### 4. Access H2 Console (Development)
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## 🔐 Authentication

The API uses JWT token-based authentication. Default users are created on startup:

| Username | Password | Role    |
|----------|----------|---------|
| admin    | admin123 | ADMIN   |
| manager  | manager123| MANAGER |
| user     | user123  | USER    |

### Login Example
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

## 📖 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/api/auth/login` | User login |
| POST   | `/api/auth/register` | User registration |
| GET    | `/api/auth/me` | Get current user |
| POST   | `/api/auth/logout` | User logout |

### Inventory Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/inventory` | Get all items with filtering |
| GET    | `/api/inventory/{id}` | Get item by ID |
| POST   | `/api/inventory` | Create new item |
| PUT    | `/api/inventory/{id}` | Update item |
| DELETE | `/api/inventory/{id}` | Delete item |
| GET    | `/api/inventory/metrics` | Get inventory metrics |
| GET    | `/api/inventory/low-stock` | Get low stock items |
| GET    | `/api/inventory/categories` | Get all categories |
| GET    | `/api/inventory/suppliers` | Get all suppliers |

### Forecasting Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/forecast/overall` | Get overall demand forecast |
| GET    | `/api/forecast/product/{id}` | Get product-specific forecast |

## 🔧 Configuration

### Database Configuration

#### Development (H2)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
```

#### Production (PostgreSQL)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/inventory_db
    username: inventory_user
    password: your_password
```

### JWT Configuration
```yaml
app:
  jwt:
    secret: your-secret-key
    expiration: 86400000 # 24 hours
```

## 📊 Sample Data

The application includes a data seeder that creates:
- 3 default users (admin, manager, user)
- 15 sample inventory items
- Various categories and suppliers

## 🧪 Testing

Run tests with:
```bash
./gradlew test
```

## 📦 Building for Production

1. Set production profile:
```bash
./gradlew bootJar -Dspring.profiles.active=prod
```

2. Run with production database:
```bash
java -jar -Dspring.profiles.active=prod build/libs/inventory-management-*.jar
```

## 🔒 Security Features

- JWT token-based authentication
- Role-based access control
- Password encryption with BCrypt
- CORS configuration
- Input validation and sanitization
- Global exception handling

## 🚀 Deployment

### Docker Deployment
```dockerfile
FROM openjdk:17-jre-slim
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
- `DATABASE_URL`: Database connection URL
- `DATABASE_USERNAME`: Database username
- `DATABASE_PASSWORD`: Database password
- `JWT_SECRET`: JWT signing secret
- `JWT_EXPIRATION`: Token expiration time

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

For support and questions, please contact the development team or create an issue in the repository.