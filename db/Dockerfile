# Stage 1: mssql-tools-installer (This stage remains unchanged)
FROM ubuntu:20.04 AS mssql-tools-installer

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    gnupg2 \
    apt-transport-https \
    ca-certificates

RUN mkdir -p /etc/apt/keyrings/ && \
    curl -fsSL https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor -o /etc/apt/keyrings/microsoft.gpg && \
    echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/microsoft.gpg] https://packages.microsoft.com/ubuntu/20.04/prod focal main" > /etc/apt/sources.list.d/mssql-release.list

RUN apt-get update && \
    ACCEPT_EULA=Y apt-get install -y mssql-tools unixodbc-dev msodbcsql17 && \
    rm -rf /var/lib/apt/lists/*

# Stage 2: Final SQL Server Image
FROM mcr.microsoft.com/mssql/server:2022-latest

# Copy mssql-tools binaries from the installer stage
COPY --from=mssql-tools-installer /opt/mssql-tools/ /opt/mssql-tools/

# --- IMPORTANT FIX HERE ---
# Copy the entire Microsoft ODBC Driver directory structure
COPY --from=mssql-tools-installer /opt/microsoft/msodbcsql17/ /opt/microsoft/msodbcsql17/

# Copy the generic unixodbc libraries (libodbc.so and related files)
# These might still be needed by some applications that link against unixODBC.
COPY --from=mssql-tools-installer /usr/lib/x86_64-linux-gnu/libodbc* /usr/lib/x86_64-linux-gnu/

# Copy ODBC configuration files (important for driver registration)
COPY --from=mssql-tools-installer /etc/odbcinst.ini /etc/
COPY --from=mssql-tools-installer /etc/odbc.ini /etc/

# Add sqlcmd to PATH for easier execution within the container
ENV PATH="$PATH:/opt/mssql-tools/bin"