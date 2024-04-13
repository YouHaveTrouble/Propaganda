# Propaganda

An announcement plugin for [Velocity](https://papermc.io/software/velocity) proxy servers.

## Usage

### Announce command

`announce "<message>" [flags]`

- `<message>`: The message to announce. Must be enclosed in double quotes.
- `[flags]`: Optional flags to customize the announcement.

Permission required to use the command: `propaganda.announce`

### Flags

- `--type` or `-t` : Type of the announcement. Possible values are `chat`, `title` and `actionbar`. If no type is
provided, only chat announcement will be sent. This flag is repeatable.
- `--server` or `-s` : The server to send the announcement to. If no server is provided, the announcement will be sent
to all servers under the proxy. This flag is repeatable.

### Formatting
Announcements support [MiniMessage](https://docs.adventure.kyori.net/minimessage.html) formatting.

Only `chat` announcements support `<newline>` tag, as no other type of announcement supports multiline messages. The
`<newline>` tag will be silently removed from any other type of announcement for compatibility.
