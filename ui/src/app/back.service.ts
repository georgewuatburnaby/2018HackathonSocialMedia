import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'
import { Observable, of } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class BackService {

  constructor(private http: HttpClient) { }

  get(q:string): Observable<Object> {
    let x = this.http.get('/comments?sort=commentId,desc')
    return x
  }
}
