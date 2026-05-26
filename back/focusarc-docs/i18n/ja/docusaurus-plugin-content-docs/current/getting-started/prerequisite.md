# 前提条件

## 必要なツール

| ツール         | バージョン   | 目的                         |
|-------------|---------|----------------------------|
| Java JDK    | 17+     | Spring Bootアプリを実行          |
| Maven       | 3.3+    | ビルドして、依存関係管理               |
| Node.js     | 20+     | Angularのサーバーを実行            |
| npm         | 11.6.2+ | フロントエンドのパッケージ管理            |
| Angular CLI | 21.2+   | フロントエンド生成と開発サーバー           |
| Docker      | any     | ローカルMongoDBとTestcontainers |
| Git         | any     | リポジトリをクローン                 |

## おすすめツール

### バックエンド
- **IntelliJ IDEA**:　Spring Bootの最高のサポート（またはVS CodeとJava Extension Pack）
- **MongoDB Compass**:　開発中に、コレクションを調べるためのGUI
- **Postman**: APIの手動テスト（コレクションは`back/focusarc-docs/static/postman_doc.json`に含まれています）

### フロントエンド
- **Webstorm**またはVSCode(Angularの拡張あり)
- **Angular DevTools**:コンポーネントと変更検出を調べるためのChrome/Firefoxの拡張

## 知識の前提
このプロジェクトは、以下の基本的な知識があることを前提とします：
- REST APIとHTTPのメソッド
- Spring Bootの基本
- NoSQLまたはドキュメントベースのデータモデリング
- Angularのコンポーネント、サービス、リアクティブフォーム