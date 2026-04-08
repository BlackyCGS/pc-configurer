import { Typography, Box } from "@mui/material";

export default function HomePage() {
    return (
        <Box sx={{ p: 4 }}>
            <Typography variant="h3">Добро пожаловать!</Typography>
            <Typography variant="h5" sx={{ mt: 2 }}>
                Вы успешно вошли в систему.
            </Typography>
        </Box>
    );
}
