package com.example.studymate.ui.localization

import java.util.Locale

private data class InformationText(val title: String, val body: String)

private val informationTranslations = mapOf(
    "en" to mapOf(
        "help_welcome" to InformationText("Welcome to the Help Center", "Use this guide to learn about StudyMate's schedule, notes, focus timer, profile, and settings. Check the information you enter before saving."),
        "help_navigation" to InformationText("Main Navigation", "Home provides access to learning features. Schedule manages activities, Notes stores study materials, Focus provides a timer, and Profile contains account and app settings."),
        "help_schedule" to InformationText("Using the Schedule", "Open Schedule, tap add, enter the activity title, category, date, and times, then save. For School Assignment tasks, use the completion control to update their status. Reminders depend on device notification permissions and Android delivery limits."),
        "help_notes" to InformationText("Using Notes", "Open Notes to create or edit an entry. Add a title and your content, then save. You can search, pin, unpin, and delete notes using the available controls."),
        "help_timer" to InformationText("Using the Study Timer", "Open Focus settings to choose a focus session or a custom timer. Set the duration and apply it, then start the timer. Pause, resume, or stop it with the available controls."),
        "help_profile" to InformationText("Profile and Settings", "Profile lets you edit your name and photo, review account information, and open app preferences, the Help Center, or About StudyMate. Server-based email and password changes are not available in this local frontend."),
        "help_tips" to InformationText("Tips for Using StudyMate", "Plan realistic study sessions, use Notes to organize key information, and use the timer to structure study and break periods. Review your progress and check details before saving."),
        "help_faq" to InformationText("Frequently Asked Questions", "To add a schedule, open Schedule and use the add button. To create or edit a note, open Notes. To change the timer, open Focus settings. Use the back arrow to return, and find app information under Profile."),
        "help_closing" to InformationText("Closing", "StudyMate helps organize learning activities with schedules, notes, and time-management tools. Use the features that suit your needs."),
        "about_intro" to InformationText("About StudyMate", "A study companion for organizing learning and productivity. StudyMate brings together tools for planning, note-taking, time management, and reviewing study habits."),
        "about_what" to InformationText("What Is StudyMate?", "StudyMate combines schedules, notes, a timer, profile settings, and available progress features in one app. It supports organization but does not guarantee academic results or replace teachers and independent learning."),
        "about_background" to InformationText("Background and Purpose", "StudyMate was created to help learners organize schedules, notes, and study time in one practical place, with clear and consistent navigation."),
        "about_objectives" to InformationText("StudyMate Goals", "Help organize time with schedules and a timer; support note-taking; encourage regular study habits; bring learning tools together; and provide a clear, easy-to-use interface."),
        "about_features" to InformationText("Key Features", "Home provides access to learning tools and summaries. Schedule supports planning, Notes stores study information, Focus provides a timer, and Profile contains user settings. Some features currently use local or sample data and do not sync online."),
        "about_benefits" to InformationText("Expected Benefits", "StudyMate aims to help organize learning activities, manage notes, and allocate study time. These are intended benefits, not guaranteed academic outcomes."),
        "about_principles" to InformationText("Development Principles", "StudyMate is developed around usability, consistent design, organized learning activities, gradual improvement, and clear communication about currently available features."),
        "about_status" to InformationText("Development Status", "StudyMate's frontend is still being refined. Some features use local state or sample data; database and API integrations can be added later. Current app version: %1\$s."),
        "about_closing" to InformationText("Closing", "StudyMate is designed to help learners manage study activities more practically. Use the available tools to build habits that fit your goals.")
    ),
    "es" to mapOf(
        "help_welcome" to InformationText("Te damos la bienvenida al centro de ayuda", "Esta guía explica las funciones de StudyMate: horarios, notas, temporizador, perfil y ajustes. Revisa los datos antes de guardarlos."),
        "help_navigation" to InformationText("Navegación principal", "Inicio ofrece acceso a las funciones de estudio. Horario organiza actividades, Notas guarda materiales, Enfoque incluye un temporizador y Perfil contiene la cuenta y los ajustes."),
        "help_schedule" to InformationText("Cómo usar el horario", "Abre Horario, pulsa añadir, introduce el título, la categoría, la fecha y las horas, y guarda. Marca las tareas escolares como completadas desde sus controles. Los recordatorios dependen de los permisos y de las limitaciones de Android."),
        "help_notes" to InformationText("Cómo usar las notas", "Abre Notas para crear o editar una nota. Añade un título y el contenido, y guarda. Puedes buscar, fijar, desfijar o eliminar notas con los controles disponibles."),
        "help_timer" to InformationText("Cómo usar el temporizador", "En los ajustes de Enfoque, elige una sesión de concentración o un temporizador personalizado. Define la duración, aplícala e inicia el temporizador. Puedes pausarlo, reanudarlo o detenerlo."),
        "help_profile" to InformationText("Perfil y ajustes", "Perfil permite editar el nombre y la foto, revisar la información de la cuenta y abrir las preferencias, el centro de ayuda o Acerca de StudyMate. Los cambios de correo y contraseña del servidor aún no están disponibles en esta versión local."),
        "help_tips" to InformationText("Consejos para usar StudyMate", "Planifica sesiones realistas, organiza la información importante en Notas y usa el temporizador para estructurar el estudio y los descansos. Revisa el progreso y los datos antes de guardar."),
        "help_faq" to InformationText("Preguntas frecuentes", "Para añadir un horario, abre Horario y pulsa añadir. Para crear o editar una nota, abre Notas. Cambia el temporizador en Enfoque. Usa la flecha para volver y consulta la información de la aplicación en Perfil."),
        "help_closing" to InformationText("Conclusión", "StudyMate ayuda a organizar las actividades de aprendizaje con horarios, notas y herramientas para gestionar el tiempo. Usa las funciones que necesites."),
        "about_intro" to InformationText("Acerca de StudyMate", "Un compañero de estudio para organizar el aprendizaje y la productividad. StudyMate reúne herramientas de planificación, notas, gestión del tiempo y hábitos de estudio."),
        "about_what" to InformationText("¿Qué es StudyMate?", "StudyMate reúne horarios, notas, temporizador, perfil y funciones de progreso disponibles. Ayuda a organizarse, pero no garantiza resultados académicos ni sustituye a docentes o al aprendizaje independiente."),
        "about_background" to InformationText("Origen y propósito", "StudyMate se creó para ayudar a los estudiantes a organizar horarios, notas y tiempo de estudio en un solo lugar práctico y con una navegación clara."),
        "about_objectives" to InformationText("Objetivos de StudyMate", "Organizar el tiempo con horarios y temporizador; facilitar las notas; fomentar hábitos regulares; reunir herramientas de estudio; y ofrecer una interfaz clara y sencilla."),
        "about_features" to InformationText("Funciones principales", "Inicio da acceso a herramientas y resúmenes. Horario permite planificar, Notas guarda información, Enfoque ofrece un temporizador y Perfil contiene ajustes. Algunas funciones usan datos locales o de ejemplo y no se sincronizan en línea."),
        "about_benefits" to InformationText("Beneficios esperados", "StudyMate pretende ayudar a organizar las actividades, gestionar notas y distribuir el tiempo de estudio. Son objetivos, no resultados académicos garantizados."),
        "about_principles" to InformationText("Principios de desarrollo", "StudyMate prioriza la facilidad de uso, un diseño coherente, la organización del estudio, la mejora gradual y la comunicación clara sobre las funciones disponibles."),
        "about_status" to InformationText("Estado del desarrollo", "La interfaz de StudyMate sigue en desarrollo. Algunas funciones usan datos locales o de ejemplo; la integración de bases de datos y API podrá añadirse más adelante. Versión actual: %1\$s."),
        "about_closing" to InformationText("Conclusión", "StudyMate está diseñado para ayudar a organizar el estudio de forma práctica. Usa las herramientas disponibles para crear hábitos acordes con tus objetivos.")
    ),
    "fr" to mapOf(
        "help_welcome" to InformationText("Bienvenue dans le centre d'aide", "Ce guide présente les fonctionnalités de StudyMate : planning, notes, minuteur, profil et paramètres. Vérifiez les informations saisies avant de les enregistrer."),
        "help_navigation" to InformationText("Navigation principale", "Accueil donne accès aux outils d'étude. Planning gère les activités, Notes conserve les contenus, Concentration propose un minuteur et Profil regroupe le compte et les paramètres."),
        "help_schedule" to InformationText("Utiliser le planning", "Ouvrez Planning, appuyez sur Ajouter, saisissez le titre, la catégorie, la date et les horaires, puis enregistrez. Marquez les devoirs comme terminés avec leur commande dédiée. Les rappels dépendent des autorisations et des limites d'Android."),
        "help_notes" to InformationText("Utiliser les notes", "Ouvrez Notes pour créer ou modifier une note. Ajoutez un titre et votre contenu, puis enregistrez. Vous pouvez rechercher, épingler, désépingler ou supprimer une note."),
        "help_timer" to InformationText("Utiliser le minuteur", "Dans les paramètres de Concentration, choisissez une session ou un minuteur personnalisé. Réglez la durée, appliquez-la et démarrez. Vous pouvez mettre en pause, reprendre ou arrêter le minuteur."),
        "help_profile" to InformationText("Profil et paramètres", "Profil permet de modifier le nom et la photo, consulter les informations du compte et ouvrir les préférences, l'aide ou À propos. Les changements d'adresse e-mail et de mot de passe côté serveur ne sont pas disponibles dans cette version locale."),
        "help_tips" to InformationText("Conseils d'utilisation", "Planifiez des séances réalistes, organisez les informations importantes dans Notes et utilisez le minuteur pour structurer étude et pauses. Vérifiez votre progression et les données avant l'enregistrement."),
        "help_faq" to InformationText("Questions fréquentes", "Pour ajouter un planning, ouvrez Planning et appuyez sur Ajouter. Pour créer ou modifier une note, ouvrez Notes. Réglez le minuteur dans Concentration. Utilisez la flèche pour revenir et consultez les informations de l'application dans Profil."),
        "help_closing" to InformationText("Conclusion", "StudyMate aide à organiser les activités d'apprentissage avec un planning, des notes et des outils de gestion du temps. Utilisez les fonctions adaptées à vos besoins."),
        "about_intro" to InformationText("À propos de StudyMate", "Un compagnon d'étude pour organiser l'apprentissage et la productivité. StudyMate réunit des outils de planification, de prise de notes, de gestion du temps et d'habitudes d'étude."),
        "about_what" to InformationText("Qu'est-ce que StudyMate ?", "StudyMate réunit planning, notes, minuteur, profil et fonctions de progression disponibles. L'application aide à s'organiser, sans garantir de résultats scolaires ni remplacer les enseignants ou l'apprentissage autonome."),
        "about_background" to InformationText("Origine et objectif", "StudyMate a été conçu pour aider les élèves à organiser planning, notes et temps d'étude en un seul endroit pratique doté d'une navigation claire."),
        "about_objectives" to InformationText("Objectifs de StudyMate", "Organiser le temps avec un planning et un minuteur ; faciliter les notes ; encourager des habitudes régulières ; réunir des outils d'étude ; et proposer une interface claire."),
        "about_features" to InformationText("Fonctionnalités principales", "Accueil donne accès aux outils et résumés. Planning aide à planifier, Notes conserve les informations, Concentration propose un minuteur et Profil regroupe les paramètres. Certaines fonctions utilisent des données locales ou d'exemple sans synchronisation en ligne."),
        "about_benefits" to InformationText("Bénéfices attendus", "StudyMate vise à aider à organiser les activités, gérer les notes et répartir le temps d'étude. Ce sont des objectifs, pas une garantie de résultats scolaires."),
        "about_principles" to InformationText("Principes de développement", "StudyMate privilégie la facilité d'utilisation, la cohérence du design, l'organisation des activités, l'amélioration progressive et une communication claire sur les fonctions disponibles."),
        "about_status" to InformationText("État du développement", "L'interface de StudyMate est encore en cours d'amélioration. Certaines fonctions utilisent des données locales ou d'exemple ; les intégrations de base de données et d'API pourront venir plus tard. Version actuelle : %1\$s."),
        "about_closing" to InformationText("Conclusion", "StudyMate est conçu pour aider les élèves à organiser leurs études. Utilisez les outils disponibles pour développer des habitudes adaptées à vos objectifs.")
    ),
    "pt" to mapOf(
        "help_welcome" to InformationText("Boas-vindas à Central de Ajuda", "Este guia apresenta os recursos do StudyMate: agenda, notas, temporizador, perfil e configurações. Confira as informações antes de salvá-las."),
        "help_navigation" to InformationText("Navegação principal", "Início dá acesso aos recursos de estudo. Agenda organiza atividades, Notas guarda conteúdos, Foco oferece um temporizador e Perfil reúne conta e configurações."),
        "help_schedule" to InformationText("Como usar a agenda", "Abra Agenda, toque em adicionar, informe título, categoria, data e horários e salve. Marque tarefas escolares como concluídas pelo controle correspondente. Lembretes dependem das permissões e dos limites do Android."),
        "help_notes" to InformationText("Como usar as notas", "Abra Notas para criar ou editar uma anotação. Adicione um título e o conteúdo e salve. Você pode pesquisar, fixar, desafixar ou excluir notas."),
        "help_timer" to InformationText("Como usar o temporizador", "Nas configurações de Foco, escolha uma sessão de concentração ou um temporizador personalizado. Defina e aplique a duração e inicie. Pause, retome ou pare conforme necessário."),
        "help_profile" to InformationText("Perfil e configurações", "Perfil permite editar nome e foto, consultar informações da conta e abrir preferências, ajuda ou Sobre o StudyMate. Alterações de e-mail e senha no servidor não estão disponíveis nesta versão local."),
        "help_tips" to InformationText("Dicas para usar o StudyMate", "Planeje sessões realistas, organize informações importantes em Notas e use o temporizador para estruturar estudo e pausas. Confira o progresso e os dados antes de salvar."),
        "help_faq" to InformationText("Perguntas frequentes", "Para adicionar uma agenda, abra Agenda e toque em adicionar. Para criar ou editar uma nota, abra Notas. Ajuste o temporizador em Foco. Use a seta para voltar e consulte as informações do app em Perfil."),
        "help_closing" to InformationText("Conclusão", "O StudyMate ajuda a organizar atividades de estudo com agenda, notas e ferramentas de gestão do tempo. Use os recursos adequados às suas necessidades."),
        "about_intro" to InformationText("Sobre o StudyMate", "Um companheiro de estudos para organizar aprendizagem e produtividade. O StudyMate reúne ferramentas de planejamento, anotações, gestão do tempo e hábitos de estudo."),
        "about_what" to InformationText("O que é o StudyMate?", "O StudyMate reúne agenda, notas, temporizador, perfil e recursos de progresso disponíveis. Ajuda na organização, mas não garante resultados acadêmicos nem substitui professores ou o estudo independente."),
        "about_background" to InformationText("Origem e propósito", "O StudyMate foi criado para ajudar estudantes a organizar agenda, notas e tempo de estudo em um só lugar prático, com navegação clara."),
        "about_objectives" to InformationText("Objetivos do StudyMate", "Organizar o tempo com agenda e temporizador; apoiar anotações; incentivar hábitos regulares; reunir ferramentas de estudo; e oferecer uma interface clara e fácil."),
        "about_features" to InformationText("Recursos principais", "Início oferece acesso a ferramentas e resumos. Agenda ajuda no planejamento, Notas guarda informações, Foco oferece um temporizador e Perfil reúne configurações. Alguns recursos usam dados locais ou de exemplo e não sincronizam on-line."),
        "about_benefits" to InformationText("Benefícios esperados", "O StudyMate busca ajudar a organizar atividades, notas e tempo de estudo. São benefícios pretendidos, não resultados acadêmicos garantidos."),
        "about_principles" to InformationText("Princípios de desenvolvimento", "O StudyMate prioriza facilidade de uso, design consistente, organização dos estudos, melhorias graduais e comunicação clara sobre os recursos disponíveis."),
        "about_status" to InformationText("Estado do desenvolvimento", "A interface do StudyMate ainda está sendo aprimorada. Alguns recursos usam dados locais ou de exemplo; integrações com banco de dados e API podem ser adicionadas depois. Versão atual: %1\$s."),
        "about_closing" to InformationText("Conclusão", "O StudyMate foi criado para ajudar estudantes a organizar os estudos de forma prática. Use os recursos disponíveis para desenvolver hábitos alinhados aos seus objetivos.")
    ),
    "de" to mapOf(
        "help_welcome" to InformationText("Willkommen im Hilfezentrum", "Dieser Leitfaden erklärt die Funktionen von StudyMate: Termine, Notizen, Fokus-Timer, Profil und Einstellungen. Prüfe deine Eingaben, bevor du sie speicherst."),
        "help_navigation" to InformationText("Hauptnavigation", "Startseite bietet Zugang zu Lernfunktionen. Termine verwaltet Aktivitäten, Notizen speichert Lerninhalte, Fokus bietet einen Timer und Profil enthält Konto- und App-Einstellungen."),
        "help_schedule" to InformationText("Termine verwenden", "Öffne Termine, tippe auf Hinzufügen, gib Titel, Kategorie, Datum und Uhrzeiten ein und speichere. Markiere Schulaufgaben über die entsprechende Aktion als erledigt. Erinnerungen hängen von Berechtigungen und Android-Beschränkungen ab."),
        "help_notes" to InformationText("Notizen verwenden", "Öffne Notizen, um einen Eintrag zu erstellen oder zu bearbeiten. Füge Titel und Inhalt hinzu und speichere. Notizen lassen sich suchen, anheften, lösen oder löschen."),
        "help_timer" to InformationText("Lern-Timer verwenden", "Wähle in den Fokus-Einstellungen eine Fokus-Sitzung oder einen benutzerdefinierten Timer. Stelle die Dauer ein, wende sie an und starte den Timer. Du kannst ihn pausieren, fortsetzen oder stoppen."),
        "help_profile" to InformationText("Profil und Einstellungen", "Im Profil kannst du Name und Foto ändern, Kontoinformationen ansehen und Einstellungen, Hilfe oder Über StudyMate öffnen. Änderungen an E-Mail und Passwort auf dem Server sind in dieser lokalen Version nicht verfügbar."),
        "help_tips" to InformationText("Tipps für StudyMate", "Plane realistische Lerneinheiten, ordne wichtige Informationen in Notizen und strukturiere Lern- und Pausenzeiten mit dem Timer. Prüfe Fortschritt und Eingaben vor dem Speichern."),
        "help_faq" to InformationText("Häufige Fragen", "Zum Hinzufügen eines Termins öffne Termine und tippe auf Hinzufügen. Notizen erstellst oder bearbeitest du unter Notizen. Den Timer stellst du unter Fokus ein. Mit dem Pfeil gehst du zurück; App-Informationen findest du im Profil."),
        "help_closing" to InformationText("Abschluss", "StudyMate hilft, Lernaktivitäten mit Terminen, Notizen und Zeitmanagement-Werkzeugen zu organisieren. Nutze die Funktionen, die zu deinen Bedürfnissen passen."),
        "about_intro" to InformationText("Über StudyMate", "Ein Lernbegleiter zur Organisation von Lernen und Produktivität. StudyMate vereint Werkzeuge für Planung, Notizen, Zeitmanagement und Lerngewohnheiten."),
        "about_what" to InformationText("Was ist StudyMate?", "StudyMate vereint Termine, Notizen, Timer, Profileinstellungen und verfügbare Fortschrittsfunktionen. Die App unterstützt die Organisation, garantiert aber keine schulischen Ergebnisse und ersetzt weder Lehrkräfte noch selbstständiges Lernen."),
        "about_background" to InformationText("Hintergrund und Zweck", "StudyMate wurde entwickelt, um Lernenden zu helfen, Termine, Notizen und Lernzeit an einem praktischen Ort mit klarer Navigation zu organisieren."),
        "about_objectives" to InformationText("Ziele von StudyMate", "Zeit mit Terminen und Timer organisieren, Notizen unterstützen, regelmäßige Lerngewohnheiten fördern, Lernwerkzeuge bündeln und eine klare, leicht bedienbare Oberfläche bieten."),
        "about_features" to InformationText("Wichtige Funktionen", "Startseite bietet Zugang zu Werkzeugen und Übersichten. Termine unterstützt die Planung, Notizen speichert Inhalte, Fokus bietet einen Timer und Profil enthält Einstellungen. Einige Funktionen verwenden lokale oder Beispieldaten ohne Online-Synchronisierung."),
        "about_benefits" to InformationText("Erwarteter Nutzen", "StudyMate soll helfen, Aktivitäten, Notizen und Lernzeit zu organisieren. Dies sind angestrebte Vorteile und keine Garantie für schulische Ergebnisse."),
        "about_principles" to InformationText("Entwicklungsprinzipien", "StudyMate setzt auf einfache Bedienung, einheitliches Design, organisierte Lernaktivitäten, schrittweise Verbesserungen und klare Angaben zu verfügbaren Funktionen."),
        "about_status" to InformationText("Entwicklungsstand", "Die StudyMate-Oberfläche wird weiter verbessert. Einige Funktionen nutzen lokale oder Beispieldaten; Datenbank- und API-Anbindungen können später ergänzt werden. Aktuelle App-Version: %1\$s."),
        "about_closing" to InformationText("Abschluss", "StudyMate soll Lernenden helfen, ihren Alltag praktischer zu organisieren. Nutze die verfügbaren Werkzeuge für Gewohnheiten, die zu deinen Zielen passen.")
    ),
    "ja" to mapOf(
        "help_welcome" to InformationText("ヘルプセンターへようこそ", "このガイドでは、StudyMate の予定、ノート、タイマー、プロフィール、設定の使い方を説明します。保存する前に入力内容を確認してください。"),
        "help_navigation" to InformationText("主なナビゲーション", "ホームから学習機能にアクセスできます。予定では活動を管理し、ノートでは学習内容を保存します。集中にはタイマーがあり、プロフィールにはアカウントとアプリの設定があります。"),
        "help_schedule" to InformationText("予定の使い方", "予定を開いて追加をタップし、タイトル、カテゴリ、日付、時間を入力して保存します。学校の課題は専用の操作で完了にできます。リマインダーは通知権限や Android の制限に左右されます。"),
        "help_notes" to InformationText("ノートの使い方", "ノートを開いて新規作成または編集します。タイトルと内容を入力して保存してください。ノートの検索、固定、固定解除、削除ができます。"),
        "help_timer" to InformationText("学習タイマーの使い方", "集中の設定で集中セッションまたはカスタムタイマーを選びます。時間を設定して適用し、タイマーを開始します。一時停止、再開、停止もできます。"),
        "help_profile" to InformationText("プロフィールと設定", "プロフィールでは名前や写真を編集し、アカウント情報を確認して、設定、ヘルプ、StudyMate についてを開けます。サーバー上のメールアドレスやパスワードの変更は、このローカル版では利用できません。"),
        "help_tips" to InformationText("StudyMate の活用方法", "無理のない学習時間を計画し、重要な情報をノートに整理して、タイマーで学習と休憩を区切りましょう。保存前に進捗と入力内容を確認してください。"),
        "help_faq" to InformationText("よくある質問", "予定を追加するには予定を開いて追加をタップします。ノートの作成や編集はノートから行います。タイマーは集中の設定で変更できます。矢印で戻り、アプリ情報はプロフィールから確認できます。"),
        "help_closing" to InformationText("おわりに", "StudyMate は予定、ノート、時間管理ツールで学習活動を整理するアプリです。必要に合った機能をご利用ください。"),
        "about_intro" to InformationText("StudyMate について", "学習と生産性を整理するための学習パートナーです。計画、ノート、時間管理、学習習慣のためのツールをまとめています。"),
        "about_what" to InformationText("StudyMate とは", "StudyMate は予定、ノート、タイマー、プロフィール設定、利用可能な進捗機能をまとめています。整理を支援しますが、学業成績を保証したり、教師や自主学習に代わったりするものではありません。"),
        "about_background" to InformationText("背景と目的", "StudyMate は、学習者が予定、ノート、学習時間を、わかりやすい操作で一か所に整理できるよう開発されました。"),
        "about_objectives" to InformationText("StudyMate の目標", "予定とタイマーで時間を整理し、ノート作成を支援し、規則的な学習習慣を促し、学習ツールを一つにまとめ、使いやすい画面を提供します。"),
        "about_features" to InformationText("主な機能", "ホームから機能と概要にアクセスできます。予定は計画、ノートは学習情報の保存、集中はタイマー、プロフィールはユーザー設定を提供します。一部はローカルまたはサンプルデータで、オンライン同期はありません。"),
        "about_benefits" to InformationText("期待される利点", "StudyMate は活動やノートの整理、学習時間の配分を支援することを目指します。学業成績を保証するものではありません。"),
        "about_principles" to InformationText("開発方針", "使いやすさ、一貫したデザイン、学習活動の整理、段階的な改善、利用可能な機能についての明確な説明を重視します。"),
        "about_status" to InformationText("開発状況", "StudyMate の画面は引き続き改善中です。一部の機能はローカル状態またはサンプルデータを使用し、データベースや API は今後統合できます。現在のアプリ バージョン: %1\$s。"),
        "about_closing" to InformationText("おわりに", "StudyMate は学習活動をより実用的に管理できるよう設計されています。目標に合った習慣づくりにご活用ください。")
    ),
    "ko" to mapOf(
        "help_welcome" to InformationText("도움말 센터에 오신 것을 환영합니다", "이 안내에서는 StudyMate의 일정, 노트, 타이머, 프로필 및 설정 기능을 설명합니다. 저장하기 전에 입력한 내용을 확인하세요."),
        "help_navigation" to InformationText("주요 탐색", "홈에서 학습 기능에 접근할 수 있습니다. 일정은 활동을 관리하고, 노트는 학습 자료를 저장합니다. 집중에는 타이머가 있으며 프로필에는 계정과 앱 설정이 있습니다."),
        "help_schedule" to InformationText("일정 사용 방법", "일정을 열고 추가를 눌러 제목, 카테고리, 날짜와 시간을 입력한 뒤 저장하세요. 학교 과제는 해당 작업으로 완료 처리할 수 있습니다. 알림은 권한과 Android 제한에 따라 달라집니다."),
        "help_notes" to InformationText("노트 사용 방법", "노트를 열어 항목을 만들거나 수정하세요. 제목과 내용을 입력한 뒤 저장할 수 있습니다. 노트를 검색하고 고정하거나 고정 해제, 삭제할 수 있습니다."),
        "help_timer" to InformationText("학습 타이머 사용 방법", "집중 설정에서 집중 세션 또는 사용자 지정 타이머를 선택하세요. 시간을 설정하고 적용한 뒤 시작합니다. 일시 중지, 재개 또는 중지도 가능합니다."),
        "help_profile" to InformationText("프로필 및 설정", "프로필에서 이름과 사진을 수정하고 계정 정보를 확인하거나 환경설정, 도움말, StudyMate 정보를 열 수 있습니다. 서버 이메일 및 비밀번호 변경은 현재 로컬 버전에서 지원되지 않습니다."),
        "help_tips" to InformationText("StudyMate 활용 팁", "현실적인 학습 시간을 계획하고 중요한 내용을 노트에 정리하며 타이머로 학습과 휴식을 나누세요. 저장하기 전에 진행 상황과 입력 내용을 확인하세요."),
        "help_faq" to InformationText("자주 묻는 질문", "일정을 추가하려면 일정을 열고 추가를 누르세요. 노트는 노트 메뉴에서 만들거나 수정할 수 있습니다. 타이머는 집중 설정에서 바꿉니다. 화살표로 돌아가고 앱 정보는 프로필에서 확인하세요."),
        "help_closing" to InformationText("마무리", "StudyMate는 일정, 노트 및 시간 관리 도구로 학습 활동을 정리하도록 돕습니다. 필요에 맞는 기능을 사용하세요."),
        "about_intro" to InformationText("StudyMate 정보", "학습과 생산성을 정리하는 학습 동반자입니다. 계획, 노트 작성, 시간 관리 및 학습 습관을 위한 도구를 제공합니다."),
        "about_what" to InformationText("StudyMate란?", "StudyMate는 일정, 노트, 타이머, 프로필 설정 및 사용 가능한 진행 기능을 모았습니다. 정리를 돕지만 학업 성과를 보장하거나 교사 및 자기주도 학습을 대신하지 않습니다."),
        "about_background" to InformationText("배경 및 목적", "StudyMate는 학습자가 일정, 노트, 학습 시간을 명확한 탐색 기능이 있는 한곳에서 실용적으로 관리하도록 개발되었습니다."),
        "about_objectives" to InformationText("StudyMate 목표", "일정과 타이머로 시간 관리를 돕고, 노트 작성을 지원하며, 규칙적인 학습 습관을 장려하고, 학습 도구를 한곳에 모아 명확하고 쉬운 화면을 제공합니다."),
        "about_features" to InformationText("주요 기능", "홈에서 학습 도구와 요약에 접근할 수 있습니다. 일정은 계획, 노트는 정보 저장, 집중은 타이머, 프로필은 사용자 설정을 제공합니다. 일부 기능은 로컬 또는 샘플 데이터를 사용하며 온라인 동기화는 제공하지 않습니다."),
        "about_benefits" to InformationText("기대 효과", "StudyMate는 활동과 노트를 정리하고 학습 시간을 배분하도록 돕는 것을 목표로 합니다. 학업 성과를 보장하지는 않습니다."),
        "about_principles" to InformationText("개발 원칙", "사용 편의성, 일관된 디자인, 학습 활동 정리, 단계적 개선, 현재 제공되는 기능에 대한 명확한 안내를 중요하게 여깁니다."),
        "about_status" to InformationText("개발 현황", "StudyMate 화면은 계속 개선 중입니다. 일부 기능은 로컬 상태나 샘플 데이터를 사용하며 데이터베이스와 API는 추후 연동할 수 있습니다. 현재 앱 버전: %1\$s."),
        "about_closing" to InformationText("마무리", "StudyMate는 학습 활동을 실용적으로 관리하도록 설계되었습니다. 목표에 맞는 습관을 만드는 데 제공되는 도구를 활용하세요.")
    )
)

fun localizedInformationTitle(language: String, key: String, sourceText: String): String =
    informationTranslations[language.take(2)]?.get(key)?.title ?: sourceText

fun localizedInformationBody(
    language: String,
    key: String,
    sourceText: String,
    argument: String? = null
): String {
    val localizedText = informationTranslations[language.take(2)]?.get(key)?.body ?: sourceText
    return if (argument == null) localizedText else String.format(Locale.forLanguageTag(language), localizedText, argument)
}
