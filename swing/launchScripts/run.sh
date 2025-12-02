# run.sh

# Check JAVA_HOME exists
if [ -z "$JAVA_HOME" ]; then
  echo "Error: JAVA_HOME is not set."
  exit 1
fi

# Check installed Java's version is lower than 8 (Version string 1.8 means 8)
JAVA_CMD="$JAVA_HOME/bin/java"
VERSION_STRING=$("$JAVA_CMD" -version 2>&1 | head -n 1 | sed -E 's/.*version "([^"]+)".*/\1/')
if [ -z "$VERSION_STRING" ]; then
  echo "Cannot determine Java version."
  exit 1
fi
# Java main version
MAJOR_VERSION=""
FIRST_PART=$(echo "$VERSION_STRING" | cut -d'.' -f1)
if [ "$FIRST_PART" = "1" ]; then
  # Format 1.X.X
  #    Cut the second part as the major version (e.g., "8" for "1.8.0_271")
  MAJOR_VERSION=$(echo "$VERSION_STRING" | cut -d'.' -f2)
else
  # Format X.X.X
  #    First part is the major version (e.g., "11" for "11.0.1")
  MAJOR_VERSION=$FIRST_PART
fi

# Detect major version lower than 8
if [ "$MAJOR_VERSION" -lt 8 ]; then
  echo "Error: Java version is lower than 8. Detected version: $VERSION_STRING"
  exit 1
fi

LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$HOME/.colonization/lib/

# Run the Java application
"$JAVA_HOME/bin/java" -cp "./colonization-swing-0.0.1.jar:$HOME/.colonization/lib/*" org.duckdns.hjow.colonization.Colonization --updator Y