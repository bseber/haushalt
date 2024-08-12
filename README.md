# Haushalt

Simple _Haushalt_ application.

## Development

### Demo Data

To create demo data for last x months, start the application with `application.demo.data.create=true`.  
Data will only be created when there are no database entries available.

### git hooks (optional)

To automate various things, the project [git hooks](https://git-scm.com/book/uz/v2/Customizing-Git-Git-Hooks)
project. You can install these with the following command:

```bash
git config core.hooksPath '.githooks'
```

The Git hooks can be found in the [.githooks](./.githooks) directory.
