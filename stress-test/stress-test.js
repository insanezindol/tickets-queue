import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 1000,
    duration: '30s',
};

export default function () {
    const userId = Math.floor(Math.random() * 100000) + 1;
    const res = http.get('http://host.docker.internal:8080/ticket?userId='+userId);
    check(res, { 'status was 200': (r) => r.status == 200 });
}
// docker run --rm -i grafana/k6:1.5.0 run - <stress-test.js