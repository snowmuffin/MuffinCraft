# MuffinCraft Plugin

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.20.4-green.svg)
![API](https://img.shields.io/badge/paper--api-1.20.4-orange.svg)
![Java](https://img.shields.io/badge/java-17-red.svg)
![License](https://img.shields.io/badge/license-MIT-yellow.svg)

> ⚠️ **Development Status**: This project is currently in active development and is not yet ready for production use.

A comprehensive Minecraft plugin that integrates with the GameHub ecosystem, featuring custom items, currency management, external warehouse systems, and automatic resource pack distribution.

## 🚀 Features

### 🎮 Core Systems
- **Custom Item Management**: Create and manage custom items with unique textures and behaviors
- **Currency System**: Digital currency management with transaction capabilities
- **External Warehouse**: Cloud-based inventory storage system
- **Resource Pack Integration**: Automatic resource pack distribution and management
- **API Integration**: Seamless integration with GameHub backend services

### 🛠️ Custom Items
- **Muffin Item**: Special consumable with custom texture and healing effects
  - Custom 16x16 pixel texture ⚠️ *Currently not displaying due to resource pack issues*
  - Healing: +2 hearts, +6 hunger, +8 saturation
  - CustomModelData: 1001
  - *Note: Currently appears as bread until resource pack is fixed*

### 💰 Economic Features
- Player balance management
- Currency transfer between players
- Integration with external payment systems
- Configurable currency symbols and default balances

### 📦 Warehouse System
- External cloud-based storage
- Item deposit and withdrawal
- Cross-server inventory synchronization
- Web-based management interface

## 🔧 Technical Architecture

### Core Components
```
com.muffincraft.plugin/
├── api/                 # External API integration
├── commands/            # Command handlers
├── config/             # Configuration management
├── inventory/          # GUI and inventory systems
├── items/              # Custom item management
├── listeners/          # Event handlers
└── services/           # Core business logic
```

### Dependencies
- **Paper API 1.20.4**: Core Minecraft server platform
- **OkHttp3**: HTTP client for API communication
- **Gson**: JSON serialization/deserialization
- **Lombok**: Code generation and boilerplate reduction

### Build System
- **Gradle**: Build automation and dependency management
- **ShadowJar**: Fat JAR generation with dependency relocation
- **Java 17**: Target runtime environment

## 📋 Requirements

### Server Requirements
- **Minecraft Version**: 1.20.4
- **Server Software**: Paper/Spigot/Bukkit
- **Java Version**: 17 or higher
- **RAM**: Minimum 2GB recommended

### External Dependencies
- GameHub NestJS Backend Server
- HTTP/HTTPS accessible web server (for resource pack hosting)
- Database access (via GameHub API)

## ⚙️ Installation

> ⚠️ **Note**: This plugin is currently in development. Installation instructions are for development/testing purposes only.

### 1. Build the Plugin

```bash
# Clone the repository
git clone https://github.com/snowmuffin/MuffinCraft.git
cd MuffinCraft

# Build using Gradle
./gradlew shadowJar

# Alternatively, use the provided scripts
# Windows
./build-and-deploy.ps1

# Linux/Mac
./build-and-deploy.sh
```

### 2. Deploy to Server

```bash
# Copy the built JAR to your plugins folder
cp build/libs/MuffinCraft.jar /path/to/minecraft/server/plugins/

# Restart your server
```

### 3. Configuration

Edit `plugins/MuffinCraft/config.yml`:

```yaml
api:
  url: 'http://your-gamehub-server.com/api'
  token: 'your-api-token-here'

resourcepack:
  url: "http://your-server.com/muffincraft-resourcepack.zip"
  sha1: "your-resourcepack-sha1-hash"
  required: true

currency:
  enable: true
  symbol: '₩'
  default_balance: 0
```

## 🎯 Commands

### Player Commands
| Command | Description | Permission | Aliases |
|---------|-------------|------------|---------|
| `/muffincraft` | Main plugin command | `muffincraft.use` | `/mc` |
| `/balance [send <player> <amount>]` | Check/transfer currency | `muffincraft.balance` | - |
| `/warehouse [list\|deposit\|withdraw]` | Manage external storage | `muffincraft.warehouse` | `/wh`, `/창고` |
| `/muffin [give <amount>\|help]` | Muffin item management | `muffincraft.muffin` | `/머핀` |

### Admin Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/muffincraft reload` | Reload configuration | `muffincraft.admin` |
| `/muffincraft sync` | Sync with backend | `muffincraft.admin` |
| `/muffin give <amount>` | Give muffin items | `muffincraft.admin.give` |

## 🔑 Permissions

### Permission Hierarchy
```yaml
muffincraft.*:                    # All permissions
├── muffincraft.use              # Basic usage (default: true)
├── muffincraft.admin            # Admin functions (default: op)
├── muffincraft.inventory        # [DEPRECATED] Inventory access
├── muffincraft.warehouse        # Warehouse access (default: true)
├── muffincraft.balance          # Currency system (default: true)
├── muffincraft.muffin           # Muffin items (default: true)
└── muffincraft.admin.give       # Item giving (default: op)
```

## 🎨 Resource Pack System

### ⚠️ Current Issue
> **Known Problem**: Resource pack is not currently applying correctly. Custom items (like muffins) may appear as their base items (bread) until this issue is resolved.

### Automatic Distribution
- Players automatically receive resource pack on join
- SHA-1 integrity verification
- Fallback mechanisms for download failures
- Support for required/optional resource packs

### Custom Assets
```
resourcepack/
├── pack.mcmeta
└── assets/minecraft/
    ├── models/item/
    │   ├── bread.json           # Modified base item
    │   └── custom/muffin.json   # Custom muffin model
    └── textures/item/custom/
        └── muffin.png           # Custom muffin texture
```

### CustomModelData Values
- **Muffin**: 1001
- **Future Items**: 1002-1999 (reserved)

## 🌐 API Integration

### GameHub Backend
The plugin integrates with a NestJS backend for:
- User authentication and authorization
- Currency transactions and balance management
- Warehouse data persistence
- Resource pack hosting and distribution
- Cross-server data synchronization

### Endpoints Used
```
GET  /api/auth/verify              # User verification
POST /api/currency/transfer        # Currency transactions
GET  /api/warehouse/items          # Warehouse inventory
POST /api/warehouse/deposit        # Item storage
GET  /api/resourcepack/download    # Resource pack distribution
```

## 🏗️ Development Setup

### Prerequisites
- Java 17 JDK
- Gradle 7.x or higher
- IDE with Lombok support (IntelliJ IDEA recommended)
- Paper 1.20.4 test server

### Development Environment
```bash
# Clone and setup
git clone https://github.com/snowmuffin/MuffinCraft.git
cd MuffinCraft

# Install dependencies
./gradlew build

# Run tests
./gradlew test

# Hot reload development (if server supports it)
./hot-reload.ps1  # Windows
./hot-reload.sh   # Linux/Mac
```

### Project Structure
```
src/main/java/com/muffincraft/plugin/
├── MuffinCraftPlugin.java       # Main plugin class
├── api/GameHubAPI.java          # External API client
├── commands/                    # Command implementations
├── config/Config.java           # Configuration manager
├── items/CustomItemManager.java # Custom item system
├── listeners/                   # Event handlers
└── services/                    # Business logic services
```

## 🧪 Testing

> ⚠️ **Current Status**: Test suite is under development

### Planned Test Coverage
- Unit tests for service classes
- Integration tests for API communication
- Mock server tests for external dependencies
- Resource pack validation tests

### Test Commands
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "CustomItemManagerTest"

# Generate test coverage report
./gradlew jacocoTestReport
```

## 📚 Documentation

### Additional Guides
- [Resource Pack Creation Guide](RESOURCEPACK_GUIDE.md)
- [Resource Pack Deployment Guide](RESOURCEPACK_DEPLOYMENT.md)
- API Documentation (Coming Soon)
- Developer Setup Guide (Coming Soon)

### Code Documentation
- Javadoc generation: `./gradlew javadoc`
- Generated docs: `build/docs/javadoc/index.html`

## 🔄 Build & Deployment

### Automated Scripts
- **Windows**: `build-and-deploy.ps1`, `quick-deploy.ps1`, `hot-reload.ps1`
- **Cross-platform**: `build-and-deploy.bat`, `quick-deploy.bat`, `hot-reload.bat`

### Build Artifacts
- **Main JAR**: `build/libs/MuffinCraft.jar`
- **Source JAR**: `build/libs/MuffinCraft-sources.jar`
- **Documentation**: `build/docs/`

### Deployment Pipeline
1. **Clean**: Remove old build artifacts
2. **Compile**: Java source compilation
3. **Test**: Run test suite (when available)
4. **Package**: Create ShadowJar with dependencies
5. **Deploy**: Copy to target server directory
6. **Backup**: Preserve previous version

## 🐛 Known Issues & Limitations

### Current Development Status
- ⚠️ **Beta Software**: Not recommended for production servers
- ⚠️ **API Dependency**: Requires GameHub backend server
- ⚠️ **Configuration**: Manual setup required for external services
- ⚠️ **Testing**: Limited test coverage during development

### Active Issues
- 🚨 **Resource Pack Not Loading**: Custom textures are currently not displaying properly
  - Muffin items appear as regular bread instead of custom texture
  - Resource pack download may be failing or not applying correctly
  - Investigation required for SHA-1 verification and pack format compatibility
  - Players may need to manually accept resource pack prompts

### Planned Improvements
- [ ] **Fix resource pack loading issues** (High Priority)
- [ ] Comprehensive test suite
- [ ] Performance optimization
- [ ] Enhanced error handling
- [ ] Database migration tools
- [ ] Admin web interface
- [ ] Multi-language support

## 🤝 Contributing

### Development Guidelines
1. Follow existing code style and conventions
2. Add appropriate Javadoc comments
3. Include unit tests for new features
4. Update documentation for API changes
5. Test with multiple Minecraft versions

### Pull Request Process
1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors & Contributors

- **GameHub Team** - *Initial development*
- **snowmuffin** - *Project maintainer*

## 🆘 Support

### Getting Help
- **Issues**: [GitHub Issues](https://github.com/snowmuffin/MuffinCraft/issues)
- **Discussions**: [GitHub Discussions](https://github.com/snowmuffin/MuffinCraft/discussions)
- **Documentation**: [Wiki](https://github.com/snowmuffin/MuffinCraft/wiki)

### Reporting Bugs
Please include:
- Minecraft version
- Plugin version
- Server software (Paper/Spigot/Bukkit)
- Error logs and stack traces
- Steps to reproduce
- **For resource pack issues**: Client-side resource pack status and any error messages

## 🔮 Roadmap

### Version 1.1.0 (Planned)
- [ ] **Fix resource pack loading and texture display issues** (Critical)
- [ ] Complete test suite implementation
- [ ] Performance optimizations
- [ ] Enhanced resource pack features
- [ ] Multi-server synchronization

### Version 1.2.0 (Future)
- [ ] Web-based administration panel
- [ ] Advanced custom item crafting
- [ ] Economy marketplace integration
- [ ] Player statistics and analytics

### Version 2.0.0 (Long-term)
- [ ] Complete UI overhaul
- [ ] Advanced mod integration
- [ ] Cross-platform compatibility
- [ ] Enterprise features

---

**Disclaimer**: This plugin is currently in active development. Features, APIs, and configurations may change significantly before the stable release. Use in production environments is not recommended at this time.
